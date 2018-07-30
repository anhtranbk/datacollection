package com.datacollection.collect.idgen;

import com.datacollection.common.config.Configurable;
import com.datacollection.common.config.Properties;
import com.datacollection.common.utils.Reflects;

import java.util.List;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface RemoteIdGenerator extends Configurable {

    long generate(List<String> seeds, long defVal);

    static RemoteIdGenerator create(Properties p) {
        RemoteIdGenerator ins = Reflects.newInstance(p.getProperty("remote.idgen.class"));
        ins.configure(p);
        return ins;
    }
}
