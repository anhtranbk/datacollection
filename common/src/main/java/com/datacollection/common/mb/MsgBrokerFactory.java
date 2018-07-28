package com.datacollection.common.mb;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface MsgBrokerFactory {

    MsgBrokerReader createReader();

    MsgBrokerWriter createWriter();
}
