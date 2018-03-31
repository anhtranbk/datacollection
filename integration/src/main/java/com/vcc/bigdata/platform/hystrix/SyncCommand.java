package com.vcc.bigdata.platform.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import java.util.concurrent.Callable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SyncCommand<T> extends HystrixCommand<T> {

    private final Callable<T> callable;
    private final T fallbackValue;
    private RuntimeException exception;

    public SyncCommand(String group, String name, Callable<T> callable, T fallbackValue) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(group))
                .andCommandKey(HystrixCommandKey.Factory.asKey(name)));
        this.callable = callable;
        this.fallbackValue = fallbackValue;
    }

    public SyncCommand(String group, String name, Callable<T> callable) {
        this(group, name, callable, null);
    }

    @Override
    protected T run() throws Exception {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            this.exception = e;
            throw e;
        }
    }

    @Override
    protected T getFallback() {
        return this.fallbackValue;
    }

    public RuntimeException getException() {
        return this.exception;
    }
}
