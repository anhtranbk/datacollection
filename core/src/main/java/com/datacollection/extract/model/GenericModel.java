package com.datacollection.extract.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericModel {
    public static final String TYPE_LINKEDIN = "linkedin";

    private String id;
    private String type;
    private Map<String, Object> post = new LinkedHashMap<>();

    public GenericModel() {
    }

    public GenericModel(String id, String type, AbstractPost post) {
        this(id, type, post.toMap());
    }

    public GenericModel(String id, String type, Map<String, Object> postMap) {
        this.id = id;
        this.type = type;
        this.post.putAll(postMap);
    }

    @Override
    public String toString() {
        return "{id=" + id + ",type=" + type + "}";
    }

    public String getId() {
        return id;
    }

    public GenericModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public GenericModel setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, Object> getPost() {
        return post;
    }

    public GenericModel setPost(Map<String, Object> post) {
        this.post = post;
        return this;
    }
}
