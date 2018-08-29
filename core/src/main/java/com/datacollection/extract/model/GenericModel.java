package com.datacollection.extract.model;

import com.datacollection.entity.Item;
import com.datacollection.entity.MetaData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericModel extends Item {
    private MetaData metadata = new MetaData();
    private final Map<String, Object> properties = new LinkedHashMap<>();

    public GenericModel() {
        super();
    }

    public GenericModel(String id, String type, AbstractPost post) {
        this(id, type, post.toMap());
    }

    public GenericModel(String id, String type, Map<String, Object> properties) {
        super(id, type);
        this.properties.putAll(properties);
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public GenericModel setProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public GenericModel putProperties(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public GenericModel setMetadata(MetaData metadata) {
        this.metadata = metadata;
        return this;
    }
}
