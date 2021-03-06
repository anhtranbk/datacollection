package com.datacollection.collect;

import com.datacollection.common.broker.BrokerFactory;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.config.Properties;
import com.datacollection.common.lifecycle.AbstractLifeCycle;
import com.datacollection.common.broker.BrokerRecordHandler;
import com.datacollection.common.broker.BrokerReader;
import com.datacollection.serde.Deserializer;
import com.datacollection.serde.Serialization;
import com.datacollection.common.utils.Reflects;
import com.datacollection.entity.Event;
import com.datacollection.metric.Counter;
import com.datacollection.metric.CounterMetrics;
import com.datacollection.metric.MetricExporter;
import com.datacollection.metric.Sl4jPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Collector extends AbstractLifeCycle implements BrokerRecordHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final Configuration conf;
    protected final Counter counter = new Counter();

    private CounterMetrics counterMetrics;
    private MetricExporter metricExporter;

    private Deserializer<Event> deserializer;
    private CollectService service;
    private BrokerReader brokerReader;

    public Collector(Configuration conf) {
        this.conf = conf;
    }

    @Override
    protected void onInitialize() {
        // init message queue
        brokerReader = createMsgBrokerReader(conf);
        brokerReader.addHandler(this);
        deserializer = Serialization.create(conf.getProperty("mb.deserializer"), Event.class).deserializer();

        // init main services
        service = new GraphCollectService(conf);

        // init monitoring
        counterMetrics = new CounterMetrics(new Sl4jPublisher(), "default-metric-group",
                "collector", counter, 1000);
        metricExporter = new MetricExporter(conf);
    }

    @Override
    public void onStart() {
        brokerReader.start();
        counterMetrics.start();
        metricExporter.start();
    }

    @Override
    public void onStop() {
        brokerReader.stop();
        counterMetrics.stop();
        metricExporter.stop();
//        service.close();
    }

    protected BrokerReader createMsgBrokerReader(Properties props) {
        BrokerFactory factory = Reflects.newInstance(props.getProperty("mb.factory.class"));
        logger.info("MsgBrokerFactory class: " + factory.getClass().getName());

        BrokerReader reader = factory.getReader();
        reader.configure(props);
        return reader;
    }

    public Properties getConf() {
        return conf;
    }

    public Counter getCounter() {
        return counter;
    }

    public CounterMetrics getCounterMetrics() {
        return counterMetrics;
    }

    public Deserializer<Event> getDeserializer() {
        return deserializer;
    }

    public CollectService getService() {
        return service;
    }

    public BrokerReader getMsgBrokerReader() {
        return brokerReader;
    }
}
