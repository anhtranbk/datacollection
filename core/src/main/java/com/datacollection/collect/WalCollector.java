package com.datacollection.collect;

import com.datacollection.collect.wal.DefaultWalFile;
import com.datacollection.collect.wal.WalFile;
import com.datacollection.collect.wal.WalWriter;
import com.datacollection.common.concurrenct.AllInOneFuture;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.io.FileHelper;
import com.datacollection.common.broker.Record;
import com.datacollection.common.broker.Records;
import com.datacollection.exc.TaskErrorExceedLimitException;
import com.datacollection.common.tasks.TaskManager;
import com.datacollection.common.utils.ThreadPool;
import com.datacollection.common.utils.Threads;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class WalCollector extends Collector {

    public static final long WAL_SIZE_LIMIT_IN_BYTES = 1024 * 1024; // 1 MB
    public static final String WAL_CODEC_SIMPLE = "simple";

    private final ThreadLocal<WalFile> walFileThreadLocal = new ThreadLocal<>();
    private String walCodec;
    private long walSizeLimit;
    private String walDataDir;
    private TaskManager taskManager;

    public WalCollector(Configuration conf) {
        super(conf);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // init collect executors
        ExecutorService executor = ThreadPool.builder()
                .setCoreSize(conf.getInt("threadpool.core.size",
                        Runtime.getRuntime().availableProcessors()))
                .setQueueSize(conf.getInt("threadpool.queue.size", 4))
                .setNamePrefix("collector-worker")
                .setDaemon(true)
                .build();
        taskManager = new TaskManager(conf, executor);

        // init wal properties
        walCodec = conf.getProperty("wal.codec", WAL_CODEC_SIMPLE);
        walSizeLimit = conf.getLong("wal.size.limit", WAL_SIZE_LIMIT_IN_BYTES);
        walDataDir = conf.getProperty("data.path") + "/wal/";
        FileHelper.checkCreateDir(walDataDir);
    }

    @Override
    public void onStart() {
        processUnhandledWalFiles();
        super.onStart();
    }

    @Override
    public void onStop() {
        logger.info("Wait for all workers finish before stopped...");
        Threads.stopThreadPool(taskManager.executor());
        super.onStop();
    }

    @Override
    public void handle(Records records) {
        WalFile wal = walFileThreadLocal.get();
        if (wal == null) {
            wal = createNewWalFile();
            walFileThreadLocal.set(wal);
        }

        try (WalWriter writer = wal.openForWrite()) {
            for (Record record : records) {
                writer.append(record.data());
            }
        }
        if (!wal.isReachedLimit()) return;

        Runnable task = newWalExecutor(wal);
        while (isNotCanceled()) {
            try {
                if (taskManager.trySubmit(task) != null) {
                    walFileThreadLocal.set(createNewWalFile());
                    break;
                }
                Threads.sleep(5);
            } catch (TaskErrorExceedLimitException e) {
                logger.error(e.getMessage(), e);
                Threads.sleep(2000);
                taskManager.reset();
            }
        }
    }

    private WalFile createNewWalFile() {
        String path = walDataDir + WalFile.createNew();
        WalFile wal = new DefaultWalFile(path, walCodec, walSizeLimit);
        walFileThreadLocal.set(wal);
        return wal;
    }

    @SuppressWarnings("ConstantConditions")
    private void processUnhandledWalFiles() {
        File dir = new File(walDataDir);
        if (dir.list() == null || dir.list().length == 0) {
            logger.info("No un-handled wal files");
            return;
        }

        logger.info(dir.list().length + " wal files need to process");
        List<Future<?>> futures = new ArrayList<>(dir.list().length);

        for (String file : dir.list()) {
            WalFile wal = new DefaultWalFile(walDataDir + file, walCodec, walSizeLimit);
            Runnable task = newWalExecutor(wal);
            while (isNotCanceled()) {
                Future<?> fut = taskManager.trySubmit(task);
                if (fut != null) {
                    futures.add(fut);
                    break;
                }
                Threads.sleep(500);
            }
        }

        Future<?> fut = AllInOneFuture.from(futures);
        while (!fut.isDone() && isNotCanceled()) {
            Threads.sleep(500);
        }
    }

    private WalCollectHandler newWalExecutor(WalFile wal) {
        return new WalCollectHandler(conf, wal, getService(), getDeserializer(), counter);
    }

    public static void main(String[] args) {
        Configuration conf = new Configuration().getSubConfiguration("collect", "WalCollector");
        new WalCollector(conf).start();
    }
}
