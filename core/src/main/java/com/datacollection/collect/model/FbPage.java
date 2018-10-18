package com.datacollection.collect.model;

import com.datacollection.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FbPage extends Profile {

    public FbPage(String id) {
        this(id, Collections.emptyMap());
    }

    public FbPage(String id, Object... keyValues) {
        this(id, Maps.initFromKeyValues(keyValues));
    }

    public FbPage(String id, Map<String, Object> properties) {
        super(TYPE_FBPAGE, id, properties);
    }

    @Override
    public String id() {
        return "fbpage_" + super.id();
    }
}
