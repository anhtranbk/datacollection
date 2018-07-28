package com.datacollection.common.mb;

import com.datacollection.common.config.Configurable;

import java.io.Closeable;
import java.io.Flushable;
import java.util.concurrent.Future;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface MsgBrokerWriter extends Configurable, Closeable, Flushable {

    Future<Long> write(byte[] b);

    default void flush() {
    }

    default void close() {
    }
}
