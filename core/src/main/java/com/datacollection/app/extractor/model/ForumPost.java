package com.datacollection.app.extractor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.avro.reflect.Nullable;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ForumPost extends Post implements Serializable {

    @JsonProperty("user_name")
    @Nullable
    public String userName;

    @JsonProperty("user_id")
    @Nullable
    public String userId;

    @Nullable
    @JsonProperty("post_time")
    public long postTime;

    @JsonProperty("url")
    @Nullable
    public String url;

    @JsonProperty("domain")
    @Nullable
    public String domain;
}
