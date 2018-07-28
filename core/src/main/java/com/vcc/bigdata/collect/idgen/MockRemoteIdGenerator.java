package com.vcc.bigdata.collect.idgen;

import com.datacollection.common.config.Properties;

import java.util.List;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class MockRemoteIdGenerator implements RemoteIdGenerator {

    @Override
    public long generate(List<String> seeds, long defVal) {
        return defVal;
    }

    @Override
    public void configure(Properties p) {
    }
}
