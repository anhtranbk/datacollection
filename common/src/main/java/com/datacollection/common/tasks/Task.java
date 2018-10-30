package com.datacollection.common.tasks;

import java.util.concurrent.Callable;

public interface Task<V> extends Runnable, Callable<V> {

    @Override
    default void run() {
        call();
    }

    @Override
    V call();
}
