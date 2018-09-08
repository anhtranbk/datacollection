package com.datacollection.extract;

import com.google.common.base.Preconditions;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.config.Properties;
import com.datacollection.common.lifecycle.LoopableLifeCycle;
import com.datacollection.common.broker.BrokerFactory;
import com.datacollection.common.broker.BrokerWriter;
import com.datacollection.common.serialize.Serialization;
import com.datacollection.common.serialize.Serializer;
import com.datacollection.common.utils.Reflects;
import com.datacollection.common.utils.ThreadPool;
import com.datacollection.common.utils.Threads;
import com.datacollection.common.utils.Utils;
import com.datacollection.entity.Event;
import com.datacollection.platform.hystrix.SyncCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public abstract class Extractor extends LoopableLifeCycle implements Runnable {

    static final String KEY_SERIALIZER = "mb.serializer";
    static final String DOC_INDEX = "index";
    static final String DOC_ORDER = "order";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Properties props;
    protected final String group;

    private ExecutorService eventLoopExecutor;
    private IndexKeeper indexKeeper;
    private BrokerWriter brokerWriter;
    private Serializer<Event> serializer;

    public Extractor(String group, Configuration config) {
        this.group = group;
        this.props = config.toSubProperties(group, getClass().getSimpleName());
        this.setSleepTime(this.props);
        this.initMessageBroker();
    }

    @Override
    protected void onInitialize() {
        this.eventLoopExecutor = ThreadPool.builder()
                .setCoreSize(1)
                .setQueueSize(4)
                .setNamePrefix("extract-event-loop")
                .build();
        this.serializer = Serialization.create(props.getProperty(KEY_SERIALIZER), Event.class).serializer();

        long indexDelay = props.getLongProperty("logging.lazy.delay.ms", 500);
        int minLines = props.getIntProperty("index.min.lines", 5);
        String indexPath = props.getProperty("data.path") + "/extract/" + getClass().getSimpleName() + ".log";
        this.indexKeeper = new IndexKeeper(indexPath, indexDelay, minLines);
    }

    @Override
    protected void onStop() {
        logger.info("Stopping event loop executors...");
        Threads.stopThreadPool(eventLoopExecutor);
        logger.info("Worker event loop stopped");

        brokerWriter.flush();
        brokerWriter.close();
    }

    @Override
    public final void run() {
        this.start();
    }

    private void initMessageBroker() {
        BrokerFactory factory = Reflects.newInstance(props.getProperty("mb.factory.class"));
        logger.info("MessageBrokerFactory class: " + factory.getClass().getName());
        this.setMsgBrokerFactory(factory);
    }

    public final void setMsgBrokerFactory(BrokerFactory factory) {
        this.brokerWriter = factory.getWriter();
        this.brokerWriter.configure(this.props);
    }

    protected final void store(Event event) {
        store(event, null);
    }

    protected final void store(Event event, Object attachment) {
        new SyncCommand<>(group, this.getClass().getSimpleName(), () -> {
            doStore(event, attachment);
            return null;
        }).execute();
    }

    private void doStore(Event event, Object attachment) {
        try {
            Preconditions.checkNotNull(event);
            byte[] b = serializer.serialize(event);
            Future<Long> fut = brokerWriter.write(b);
            eventLoopExecutor.submit(() -> {
                try {
                    long queueOrder = fut.get();
                    onRecordProcessed(event, queueOrder, attachment);
                } catch (Exception exc) {
                    logger.error("Write to message queue error for: " + event.getId(), exc);
                    Utils.systemError(exc);
                }
            });
        } catch (NullPointerException | RejectedExecutionException | IOException ignored) {
        }
    }

    /**
     * Default store model's id and queue order, sub-class should implement to add more works
     *
     * @param queueOrder order of record in message queue
     * @param event      Event object contains extracted data
     * @param attachment object attached before send record to Message Broker to retrieve info
     */
    protected void onRecordProcessed(Event event, long queueOrder, Object attachment) {
        storeIndex(event.getId(), queueOrder);
    }

    protected final void storeIndex(String docIndex, long docOrder) {
        try {
            indexKeeper.put(DOC_INDEX, docIndex);
            indexKeeper.put(DOC_ORDER, docOrder);
            indexKeeper.persist();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected final String loadIndex() {
        return indexKeeper.get(DOC_INDEX);
    }

    @SuppressWarnings("SameParameterValue")
    protected final String loadIndex(String defVal) {
        return Optional.ofNullable(loadIndex()).orElse(defVal);
    }
}
