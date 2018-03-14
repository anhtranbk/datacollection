package com.vcc.bigdata.common.io;

import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.common.utils.Utils;

import java.io.IOException;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class PersistentProperties extends Properties {

    public static PersistentProperties open(String path) {
        return new PersistentProperties(path);
    }

    private final Properties impl;
    private final String path;

    private PersistentProperties(String path) {
        this.path = path;
        this.impl = Utils.loadPropsOrDefault(path);
    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        return impl.setProperty(key, value);
    }

    @Override
    public String getProperty(String key) {
        return impl.getProperty(key);
    }

    /**
     * Commit all changes to persistent storage
     * @throws IOException an error occur
     */
    public void commit() throws IOException {
        Utils.writeProps(path, impl);
    }
}
