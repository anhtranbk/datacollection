package com.datacollection.extract;

import com.google.common.base.Preconditions;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.lifecycle.LoopableLifeCycle;
import com.datacollection.common.broker.BrokerFactory;
import com.datacollection.common.broker.BrokerWriter;
import com.datacollection.serde.Serialization;
import com.datacollection.serde.Serializer;
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
import java.util.concurrent.TimeUnit;

public abstract class Extractor extends LoopableLifeCycle implements Runnable {

    private static final String KEY_SERIALIZER = "mb.serializer";
    private static final String DOC_INDEX = "index";
    private static final String DOC_ORDER = "order";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Configuration conf;

    private final String group;
    private final String name;

    private ExecutorService workerExecutor;
    private IndexKeeper indexKeeper;
    private BrokerWriter brokerWriter;
    private Serializer<Event> serializer;

    public Extractor(String group, Configuration conf) {
        this.group = group;
        this.name = this.getClass().getName();
        this.conf = conf.getSubConfiguration(group, getClass().getSimpleName());

        this.setSleepTime(this.conf);
        this.initMessageBroker();
    }

    @Override
    protected void onInitialize() {
        this.workerExecutor = ThreadPool.builder()
                .setCoreSize(1)
                .setQueueSize(4)
                .setNamePrefix("extractor-" + name)
                .build();
        this.serializer = Serialization.create(conf.getProperty(KEY_SERIALIZER), Event.class).serializer();

        long indexDelay = conf.getLong("logging.lazy.delay.ms", 500);
        int minLines = conf.getInt("index.min.lines", 5);
        String indexPath = conf.getProperty("data.path") + "/extract/" + name + ".log";
        this.indexKeeper = new IndexKeeper(indexPath, indexDelay, minLines);
    }

    @Override
    protected void onStop() {
        logger.info("Stopping worker executor...");
        boolean stopOk = Threads.stopThreadPool(workerExecutor, 5, TimeUnit.MINUTES);
        if (stopOk) {
            logger.info("Worker executor stopped");
        } else {
            logger.error("Could not stop worker executor...");
        }

        brokerWriter.flush();
        brokerWriter.close();
    }

    @Override
    public final void run() {
        this.start();
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    private void initMessageBroker() {
        BrokerFactory factory = Reflects.newInstance(conf.getProperty("mb.factory.class"));
        logger.info("BrokerFactory class: " + factory.getClass().getName());
        this.setBrokerFactory(factory);
    }

    public final void setBrokerFactory(BrokerFactory factory) {
        this.brokerWriter = factory.getWriter();
        this.brokerWriter.configure(this.conf);
    }

    protected final void sendEvent(Event event) {
        sendEvent(event, null);
    }

    protected final void sendEvent(Event event, Object attachment) {
        new SyncCommand<>(group, name, () -> {
            doSendEvent(event, attachment);
            return null;
        }).execute();
    }

    private void doSendEvent(Event event, Object attachment) {
        try {
            Preconditions.checkNotNull(event);
            byte[] b = serializer.serialize(event);
            workerExecutor.submit(() -> {
                try {
                    Future<Long> fut = brokerWriter.write(b);
                    long queueOrder = fut.get();
                    onEventProcessed(event, queueOrder, attachment);
                } catch (Exception exc) {
                    logger.error("Send event error: " + event, exc);
                    Utils.systemError(exc);
                }
            });
        } catch (RejectedExecutionException ignored) {
        } catch (NullPointerException | IOException ex) {
            logger.warn("Event ignored due to serialization error: " + event);
        }
    }

    /**
     * Default only store event's ID and queue order, subclasses should implement to add more works
     *
     * @param queueOrder order of event record in message queue
     * @param event      Event object contains extracted data
     * @param attachment attached object that contains extra info to be process by extractors
     *                   before sending Event to Message Broker to retrieve info
     */
    protected void onEventProcessed(Event event, long queueOrder, Object attachment) {
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
