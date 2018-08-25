package com.datacollection.common.broker;

import com.datacollection.common.config.Properties;
import com.google.common.util.concurrent.Futures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock message queue writer use for testing
 */
public class MockBrokerWriter implements BrokerWriter {

    private static final Logger logger = LoggerFactory.getLogger(MockBrokerWriter.class);
    private final AtomicLong counter = new AtomicLong();

    @Override
    public void configure(Properties p) {
    }

    @Override
    public Future<Long> write(byte[] b) {
        logger.debug("Write mock message: " + new String(b));
        return Futures.immediateFuture(counter.incrementAndGet());
    }
}
