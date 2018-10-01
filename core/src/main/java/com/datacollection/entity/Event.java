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

    private Metadata metadata = new Metadata();
    private Map<String, Object> properties;
    private String source;

    public Event() {
        super();
        this.properties = new LinkedHashMap<>();
    }

    public Event(String id, String type, Map<String, Object> properties) {
        super(id, type);
        this.properties = properties;
    }

    @Override
    public Event setId(String id) {
        super.setId(id);
        return this;
    }

    @Override
    public Event setType(String type) {
        super.setType(type);
        return this;
    }

    @Override
    public Event setScope(String scope) {
        super.setScope(scope);
        return this;
    }

    public String getSource() {
        return source;
    }

    public Event setSource(String source) {
        this.source = source;
        return this;
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

    public Metadata getMetadata() {
        return metadata;
    }

    public Event setMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }
}
