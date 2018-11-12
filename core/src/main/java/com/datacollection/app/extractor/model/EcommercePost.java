package com.datacollection.app.extractor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EcommercePost extends Post {

    public String full_name;
    public String user_name;
    public String sky_id;
    public String address;
    public String fax;
    public String birth_day;
    public String gender;
    public String company;
    public String source_name;
}
