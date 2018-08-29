package com.datacollection.entity;

import java.util.List;

public class MetaData extends Item {

    private String name;
    private String description;
    private List<String> tags;
    private List<String> systemTags;
    private boolean enabled;
    private boolean hidden;
    private boolean readOnly;

    public String getName() {
        return name;
    }

    public MetaData setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MetaData setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public MetaData setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public List<String> getSystemTags() {
        return systemTags;
    }

    public MetaData setSystemTags(List<String> systemTags) {
        this.systemTags = systemTags;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public MetaData setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public MetaData setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public MetaData setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }
}
