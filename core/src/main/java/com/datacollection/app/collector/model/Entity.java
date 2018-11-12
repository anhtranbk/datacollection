package com.datacollection.app.collector.model;

import com.datacollection.common.collect.Maps;
import com.datacollection.common.utils.Strings;

import java.util.Collections;
import java.util.Map;

/**
 * Normalized entity
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Entity extends BaseEntity {

    public Entity(String id, String label) {
        this(id, label, Collections.emptyMap());
    }

    public Entity(String id, String label, Object... keyValues) {
        this(id, label, Maps.initFromKeyValues(keyValues));
    }

    public Entity(String id, String label, Map<String, Object> properties) {
        super(id, label, properties);
    }

    @Override
    public BaseEntity setId(String id) {
        if (Strings.isNonEmpty(id)) {
            return super.setId(id.trim().toLowerCase().replace("|", ""));
        }
        return super.setId(id);
    }
}
