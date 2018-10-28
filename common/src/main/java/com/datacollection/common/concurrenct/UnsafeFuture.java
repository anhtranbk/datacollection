package com.datacollection.common.concurrenct;

import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * A future can be invoke get() without checked exception
 */
public interface UnsafeFuture<V> extends ListenableFuture<V> {

    @Override
    V get();

    @Override
    V get(long timeout, @NotNull TimeUnit unit);
}
