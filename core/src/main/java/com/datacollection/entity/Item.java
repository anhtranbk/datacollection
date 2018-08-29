package com.datacollection.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Item {

    private String id;
    private String type;
    private String scope;

    public Item() {
    }

    public Item(String id, String type) {
        this(id, type, "default");
    }

    public Item(String id, String type, String scope) {
        this.id = id;
        this.type = type;
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public Item setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Item setType(String type) {
        this.type = type;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public Item setScope(String scope) {
        this.scope = scope;
        return this;
    }
}
