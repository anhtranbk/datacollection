package com.datacollection.collect;

import com.google.common.util.concurrent.ListenableFuture;
import com.datacollection.entity.Event;

import java.io.Closeable;

public interface CollectService extends Closeable {

    ListenableFuture<?> collect(Event event);

    void close();
}
