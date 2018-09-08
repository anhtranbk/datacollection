package com.datacollection.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Event extends Item {
    private MetaData metadata = new MetaData();
    private Map<String, Object> properties;

    public Event() {
        super();
        this.properties = new LinkedHashMap<>();
    }

    public Event(String id, String type, Map<String, Object> properties) {
        super(id, type);
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public Event setProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public Event putProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public Event setProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public Event setMetadata(MetaData metadata) {
        this.metadata = metadata;
        return this;
    }
}
