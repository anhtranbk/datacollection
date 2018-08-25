package com.datacollection.platform.kafka;

import com.datacollection.common.broker.BrokerFactory;
import com.datacollection.common.broker.BrokerReader;
import com.datacollection.common.broker.BrokerWriter;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class KafkaBrokerFactory implements BrokerFactory {

    @Override
    public BrokerReader getReader() {
        return new KafkaBrokerReader();
    }

    @Override
    public BrokerWriter getWriter() {
        return new KafkaBrokerWriter();
    }
}
