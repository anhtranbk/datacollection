package com.datacollection.extract.model;

import com.datacollection.entity.Item;
import com.datacollection.entity.MetaData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericModel extends Item {
    private MetaData metadata = new MetaData();
    private Map<String, Object> post = new LinkedHashMap<>();

    public GenericModel() {
        super();
    }

    public GenericModel(String id, String type, AbstractPost post) {
        this(id, type, post.toMap());
    }

    public GenericModel(String id, String type, Map<String, Object> postMap) {
        super(id, type);
        this.post.putAll(postMap);
    }

    public Map<String, Object> getPost() {
        return post;
    }

    public GenericModel setPost(Map<String, Object> post) {
        this.post = post;
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
