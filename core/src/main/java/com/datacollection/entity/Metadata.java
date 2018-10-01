package com.datacollection.entity;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class Metadata extends Item {

    private String name;
    private String description;
    private List<String> tags;
    private List<String> systemTags;
    private boolean enabled;
    private boolean hidden;
    private boolean readOnly;

    public Metadata() {
        this.tags = Collections.emptyList();
        this.systemTags = Collections.emptyList();
        this.enabled = true;
        this.hidden = false;
        this.readOnly = true;
    }

    public String getName() {
        return name;
    }

    public Metadata setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Metadata setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public Metadata setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public List<String> getSystemTags() {
        return systemTags;
    }

    public Metadata setSystemTags(List<String> systemTags) {
        this.systemTags = systemTags;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Metadata setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Metadata setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public Metadata setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }
}
