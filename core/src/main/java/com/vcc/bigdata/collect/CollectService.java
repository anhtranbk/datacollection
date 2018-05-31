package com.vcc.bigdata.collect;

import com.google.common.util.concurrent.ListenableFuture;
import com.vcc.bigdata.extract.model.GenericModel;

import java.io.Closeable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface CollectService extends Closeable {

    ListenableFuture<?> collect(GenericModel genericModel);

    void close();
}