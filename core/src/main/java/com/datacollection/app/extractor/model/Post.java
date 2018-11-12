package com.datacollection.app.extractor.model;

import org.apache.avro.reflect.Nullable;

public abstract class Post {

    public static Post empty() {
        return new Post() {};
    }

    @Nullable
    public String content;
}
