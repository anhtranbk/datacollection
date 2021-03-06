package com.datacollection.collect;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.datacollection.collect.wal.WalException;
import com.datacollection.collect.wal.WalFile;
import com.datacollection.collect.wal.WalReader;
import com.datacollection.common.concurrenct.AllInOneFuture;
import com.datacollection.common.config.Properties;
import com.datacollection.common.io.FileHelper;
import com.datacollection.serde.Deserializer;
import com.datacollection.common.utils.Threads;
import com.datacollection.common.utils.Utils;
import com.datacollection.entity.Event;
import com.datacollection.metric.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
class WalCollectHandler implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final WalFile walFile;
    private final CollectService service;
    private final Deserializer<Event> deserializer;
    private final Counter counter;

    private int total;
    private long startTime;
    private final String shortName;
    private final boolean asyncMode;
    private final int retries;

    /**
     * @param props        application properties
     * @param walFile      abstract layer of physical file that contains data to need to be processed
     * @param service      instance of collect service
     * @param deserializer used to deserialize data raw data to GenericModel object
     * @param counter      used for global metric to show number records was processed per second
     */
    public WalCollectHandler(Properties props,
                             WalFile walFile,
                             CollectService service,
                             Deserializer<Event> deserializer,
                             Counter counter) {
        Preconditions.checkArgument(walFile.exists(), walFile.absolutePath() + " did not exists");

        this.walFile = walFile;
        this.service = service;
        this.deserializer = deserializer;
        this.counter = counter;

        this.shortName = FileHelper.getFileName(walFile.absolutePath());
        this.total = 0;
        this.asyncMode = props.getBool("wal.handler.async.mode", false);
        this.retries = props.getInt("wal.handler.retries", 3);
    }

    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        if (asyncMode) {
            collectAsync();
        } else {
            collectSync();
        }
    }

    private void collectSync() {
        try (WalReader reader = walFile.openForRead()) {
            byte[] data;
            while ((data = reader.next()) != null) {
                try {
                    for (int c = 0; c < retries; c++) {
                        try {
                            handleRecord(data).get(60, TimeUnit.SECONDS);
                            break;
                        } catch (ExecutionException e) {
                            logger.error("Process record error, retries = " + c);
                            Threads.sleep(500);
                        }
                    }
                    total++;
                    counter.inc();
                } catch (Exception e) {
                    logger.error("Process record error: " + rawDataToString(data), e);
                    throw new RuntimeException(e);
                }
            }
            doOnSuccess();
        } catch (WalException e) {
            logger.error(Utils.currentThreadName() + "Handle WAL error: " + shortName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void collectAsync() {
        List<Future<?>> futures = new LinkedList<>();
        try (WalReader reader = walFile.openForRead()) {
            byte[] data;
            while ((data = reader.next()) != null) {
                futures.add(handleRecord(data));
                total++;
                counter.inc();
            }

            rx.Observable.from(AllInOneFuture.from(futures))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(o -> doOnSuccess());
        } catch (WalException e) {
            logger.error(Utils.currentThreadName() + "Handle WAL error: " + shortName, e);
        }
    }

    private void doOnSuccess() {
        long took = System.currentTimeMillis() - startTime;
        if (walFile.delete()) {
            logger.info(Utils.currentThreadName() + "Successfully handled WAL: " + shortName
                    + ", total records: " + total
                    + ", took: " + ((float) took / 1000));
        }
    }

    private Future<?> handleRecord(byte[] data) {
        Event event = deserialize(data);
        return event != null ? service.collect(event) : Futures.immediateFuture(0);
    }

    private Event deserialize(byte[] data) {
        try {
            return deserializer.deserialize(data);
        } catch (IOException | NullPointerException e) {
            logger.warn("Deserialize record error", e);
        }
        return null;
    }

    private static String rawDataToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
