package com.datacollection.serde;

import java.io.IOException;
import java.io.OutputStream;

public interface Serializer<T> {

    void serialize(T input, OutputStream out) throws IOException;

    byte[] serialize(T input) throws IOException;

    default String serializeAsString(T raw) throws IOException {
        throw new UnsupportedOperationException();
    }
}
