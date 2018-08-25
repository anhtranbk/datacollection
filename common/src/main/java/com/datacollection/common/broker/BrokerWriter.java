package com.datacollection.common.broker;

import com.datacollection.common.config.Configurable;

import java.io.Closeable;
import java.io.Flushable;
import java.util.concurrent.Future;

public interface BrokerWriter extends Configurable, Closeable, AutoCloseable, Flushable {

    Future<Long> write(byte[] b);

    default void flush() {
    }

    default void close() {
    }
}
