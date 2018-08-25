package com.datacollection.common.broker;

import com.datacollection.common.config.Properties;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractBrokerReader implements BrokerReader {

    private Properties props;
    private final List<BrokerRecordHandler> handlers = new LinkedList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void configure(Properties props) {
        if (isRunning()) throw new IllegalStateException("Cannot configure a consumer after it started");
        this.props = props;
    }

    @Override
    public final void start() {
        this.running.set(true);
        doStart();
    }

    @Override
    public final void stop() {
        this.running.set(false);
        doStop();
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    public final void addHandler(BrokerRecordHandler handler) {
        handlers.add(handler);
    }

    public Properties properties() {
        return this.props;
    }

    public List<BrokerRecordHandler> handlers() {
        return this.handlers;
    }

    protected abstract void doStart();

    protected abstract void doStop();

    protected void invokeHandlers(Records records) {
        for (BrokerRecordHandler handler : handlers()) {
            handler.handle(records);
        }
    }
}
