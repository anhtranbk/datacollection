package com.datacollection.app.collector.model;

import com.datacollection.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Organization extends Profile {

    public Organization(String type, String name) {
        this(type, name, Collections.emptyMap());
    }

    public Organization(String type, String name, Object... keyValues) {
        this(type, name, Maps.initFromKeyValues(keyValues));
    }

    public Organization(String type, String name, Map<String, Object> properties) {
        super(type, name, properties);
    }
}
