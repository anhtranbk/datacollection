package com.datacollection.serde;

import java.io.IOException;
import java.io.InputStream;

public interface Deserializer<T> {

    T deserialize(InputStream in) throws IOException;

    T deserialize(byte[] serialized) throws IOException;
}
