package com.datacollection.collect.model;

import com.datacollection.common.collect.Maps;

import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract model of elements in graph model (entity and relationship)
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class GraphElement {

    private final Map<String, Object> properties = new TreeMap<>();

    public GraphElement(Map<String, Object> properties) {
        properties.forEach(this::putProperty);
    }

    public Map<String, Object> properties() {
        return properties;
    }

    public Object property(String key) {
        return this.properties.get(key);
    }

    public void putProperty(String key, Object value) {
        Maps.putIfNotNullOrEmpty(properties, key, value);
    }

    public void putProperties(Map<String, Object> map) {
        map.forEach(this::putProperty);
    }
}
