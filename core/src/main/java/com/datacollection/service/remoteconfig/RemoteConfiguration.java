package com.datacollection.service.remoteconfig;

import com.datacollection.common.config.Configuration;
import com.datacollection.common.config.Properties;

import java.util.Map;

public abstract class RemoteConfiguration extends Properties {

    public abstract String getProperty(String key);

    public abstract String setProperty(String key, String value);

    public abstract Map<String, String> getPropertiesByPrefix(String prefix);

    public abstract void setProperties(Map<String, String> props);

    public static RemoteConfiguration create(Configuration conf) {
        return new ElasticRemoteConfig(conf);
    }
}
