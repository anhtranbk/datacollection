package com.datacollection.collect.model;

import com.datacollection.common.utils.Hashings;
import com.datacollection.common.utils.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class History extends BaseEntity {

    public History(String type, String source, String url) {
        this(type, source, url, Collections.emptyMap());
    }

    public History(String type, String source, String url, Object... keyValues) {
        this(type, source, url, Maps.initFromKeyValues(keyValues));
    }

    public History(String type, String source, String url, Map<String, Object> properties) {
        super(Hashings.sha1AsBase64(url, false), null, properties);
        this.putProperty("url", url);
        this.putProperty("src", source);
        this.putProperty("type", type);
    }

    public String type() {
        return this.property("type").toString();
    }

    public String source() {
        return this.property("src").toString();
    }

    public String url() {
        return this.property("url").toString();
    }

    public String content() {
        return this.properties().getOrDefault("content", "").toString();
    }
}
