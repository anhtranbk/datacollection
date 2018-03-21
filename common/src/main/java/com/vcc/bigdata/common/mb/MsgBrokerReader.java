package com.vcc.bigdata.common.mb;

import com.vcc.bigdata.common.config.Configurable;
import com.vcc.bigdata.common.config.Properties;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface MsgBrokerReader extends Configurable {

    void configure(Properties p);

    void start();

    void stop();

    boolean running();

    void addHandler(MsgHandler handler);
}
