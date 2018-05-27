package com.vcc.bigdata.metric;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Counting {

    long getCount();

    static Counting from(Number number) {
        return number::longValue;
    }
}
