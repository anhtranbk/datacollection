package com.vcc.bigdata.collect.model;

import com.google.common.base.Preconditions;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Gender extends Entity {

    public static final String MALE = "m";
    public static final String FEMALE = "f";

    public Gender(String id) {
        super(null, "gender");
        Preconditions.checkArgument(MALE.equals(id) || FEMALE.equals(id),
                "Gender must only be m (male) or f (female)");
        setId(id);
    }
}
