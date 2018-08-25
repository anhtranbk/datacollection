package com.datacollection.common.broker;

import com.datacollection.common.config.Configurable;
import com.datacollection.common.config.Properties;

public interface BrokerReader extends Configurable {

    void configure(Properties p);

    void start();

    void stop();

    boolean isRunning();

    void addHandler(BrokerRecordHandler handler);
}
