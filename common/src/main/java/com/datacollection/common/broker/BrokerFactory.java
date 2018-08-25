package com.datacollection.common.broker;

public interface BrokerFactory {

    BrokerReader getReader();

    BrokerWriter getWriter();
}
