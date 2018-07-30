package com.datacollection.collect.wal;

import java.io.Closeable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface WalWriter extends Closeable {

    void append(byte[] data);

    @Override
    void close();
}
