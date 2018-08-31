package com.datacollection.extract.model;

import com.datacollection.common.utils.JsonUtils;
import org.apache.avro.reflect.Nullable;

import java.util.Map;

public abstract class AbstractPost {

    public static AbstractPost empty() {
        return new AbstractPost() {};
    }

    @Nullable
    public String content;

    public String toJson() {
        return JsonUtils.toJson(this);
    }

    public Map<String, Object> toMap() {
        return JsonUtils.toMap(this);
    }
}
