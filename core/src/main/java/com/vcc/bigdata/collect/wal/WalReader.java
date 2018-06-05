package com.vcc.bigdata.collect.wal;

import java.io.Closeable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface WalReader extends Closeable {

    byte[] next();

    @Override
    void close();
}
