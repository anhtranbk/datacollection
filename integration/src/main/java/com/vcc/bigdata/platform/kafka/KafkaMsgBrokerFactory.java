package com.vcc.bigdata.platform.kafka;

import com.vcc.bigdata.common.mb.MsgBrokerFactory;
import com.vcc.bigdata.common.mb.MsgBrokerReader;
import com.vcc.bigdata.common.mb.MsgBrokerWriter;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class KafkaMsgBrokerFactory implements MsgBrokerFactory {

    @Override
    public MsgBrokerReader createReader() {
        return new KafkaMsgBrokerReader();
    }

    @Override
    public MsgBrokerWriter createWriter() {
        return new KafkaMsgBrokerWriter();
    }
}
