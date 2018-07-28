package com.vcc.bigdata.collect.model;

import com.datacollection.common.utils.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * Created by kumin on 28/11/2017.
 */
public class BaseEntity extends GraphElement {

    private String id;
    private String label;

    public BaseEntity(String id, String label) {
        this(id, label, Collections.emptyMap());
    }

    public BaseEntity(String id, String label, Object... keyValues) {
        this(id, label, Maps.initFromKeyValues(keyValues));
    }

    public BaseEntity(String id, String label, Map<String, Object> properties) {
        super(properties);
        this.setId(id);
        this.setLabel(label);
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }

    public BaseEntity setId(String id) {
        this.id = id;
        return this;
    }

    public BaseEntity setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return label() + ":" + id();
    }
}
