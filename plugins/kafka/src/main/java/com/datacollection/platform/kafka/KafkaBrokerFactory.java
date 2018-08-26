package com.datacollection.platform.kafka;

import com.datacollection.common.broker.BrokerFactory;
import com.datacollection.common.broker.BrokerReader;
import com.datacollection.common.broker.BrokerWriter;

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
