package com.datacollection.collect.model;

import com.datacollection.common.utils.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * Created by kumin on 28/11/2017.
 */
public class Relationship extends GraphElement {

    private final String name;

    public Relationship(String name) {
        this(name, Collections.emptyMap());
    }

    public Relationship(String name, Object... keyValues) {
        this(name, Maps.initFromKeyValues(keyValues));
    }

    public Relationship(String name, Map<String, Object> properties) {
        super(properties);
        this.name = name;
    }

    public String name() {
        return name;
    }

    public static Relationship forName(String name) {
        return new Relationship(name);
    }
}
