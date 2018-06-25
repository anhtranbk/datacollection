package com.vcc.bigdata.collect.history;

import com.google.common.util.concurrent.ListenableFuture;
import com.vcc.bigdata.collect.model.History;
import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.common.utils.Reflects;

import java.io.Closeable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface HistoryStorage extends Closeable {

    ListenableFuture<History> addHistory(String uid, History history);

    Iterable<History> findHistoryByUid(String uid);

    @Override
    default void close() {
    }

    static HistoryStorage create(Properties p) {
        return Reflects.newInstance(p.getProperty("history.storage.class"),
                new Class<?>[]{Properties.class}, p);
    }
}
