package com.datacollection.common.broker;

public class MockBrokerFactory implements BrokerFactory {

    @Override
    public BrokerReader getReader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BrokerWriter getWriter() {
        return new MockBrokerWriter();
    }
}
