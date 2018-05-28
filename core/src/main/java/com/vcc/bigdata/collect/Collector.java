package com.vcc.bigdata.collect;

import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.common.lifecycle.AbstractLifeCycle;
import com.vcc.bigdata.common.mb.MsgBrokerFactory;
import com.vcc.bigdata.common.mb.MsgHandler;
import com.vcc.bigdata.common.mb.MsgBrokerReader;
import com.vcc.bigdata.common.serialize.Deserializer;
import com.vcc.bigdata.common.serialize.Serialization;
import com.vcc.bigdata.common.utils.Reflects;
import com.vcc.bigdata.extract.model.GenericModel;
import com.vcc.bigdata.metric.Counter;
import com.vcc.bigdata.metric.CounterMetrics;
import com.vcc.bigdata.metric.MetricExporter;
import com.vcc.bigdata.metric.Sl4jPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class Collector extends AbstractLifeCycle implements MsgHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Properties props;
    protected final Counter counter = new Counter();
    private CounterMetrics counterMetrics;
    private MetricExporter metricExporter;

    private Deserializer<GenericModel> deserializer;
    private CollectService service;
    private MsgBrokerReader msgBrokerReader;

    public Collector(Properties props) {
        this.props = props;
    }

    @Override
    protected void onInitialize() {
        // init message queue
        msgBrokerReader = createMsgBrokerReader(props);
        msgBrokerReader.addHandler(this);
        deserializer = Serialization.create(props.getProperty("mb.deserializer"),
                GenericModel.class).deserializer();

        // init main services
        service = new GraphCollectService(props);

        // init monitoring
        counterMetrics = new CounterMetrics(new Sl4jPublisher(), "default-metric-group",
                "collector", counter, 1000);
        metricExporter = new MetricExporter(props);
    }

    @Override
    public void onStart() {
        msgBrokerReader.start();
        counterMetrics.start();
        metricExporter.start();
    }

    @Override
    public void onStop() {
        msgBrokerReader.stop();
        counterMetrics.stop();
        metricExporter.stop();
//        service.close();
    }

    protected MsgBrokerReader createMsgBrokerReader(Properties props) {
        MsgBrokerFactory factory = Reflects.newInstance(props.getProperty("mb.factory.class"));
        logger.info("MsgBrokerFactory class: " + factory.getClass().getName());

        MsgBrokerReader reader = factory.createReader();
        reader.configure(props);
        return reader;
    }

    public Properties getProps() {
        return props;
    }

    public Counter getCounter() {
        return counter;
    }

    public CounterMetrics getCounterMetrics() {
        return counterMetrics;
    }

    public Deserializer<GenericModel> getDeserializer() {
        return deserializer;
    }

    public CollectService getService() {
        return service;
    }

    public MsgBrokerReader getMsgBrokerReader() {
        return msgBrokerReader;
    }
}
