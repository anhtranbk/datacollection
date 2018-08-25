package com.datacollection.common.broker;

public class MultiBrokerFactory implements BrokerFactory {

    @Override
    public BrokerReader getReader() {
        throw new UnsupportedOperationException("Multi-broker only support for writing");
    }

    @Override
    public BrokerWriter getWriter() {
        return new MultiBrokerWriter();
    }
}
