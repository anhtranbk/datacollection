package com.datacollection.extract;

import java.io.Closeable;
import java.util.Iterator;

public interface DataStream<T> extends Closeable, Iterator<T> {

    boolean hasNext();

    T next();

    void close();
}
