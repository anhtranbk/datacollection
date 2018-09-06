package com.datacollection.collect.transform;

import com.datacollection.common.utils.JsonUtils;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.datacollection.common.config.Properties;
import com.datacollection.common.broker.BrokerFactory;
import com.datacollection.common.broker.BrokerReader;
import com.datacollection.common.broker.BrokerWriter;
import com.datacollection.entity.GenericModel;

import java.util.concurrent.Future;

public class TransformTester implements BrokerFactory {

    private final Gson gson = new Gson();
    private final DataTransformer transformer;

    public TransformTester(DataTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    public BrokerReader getReader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BrokerWriter getWriter() {
        return new BrokerWriter() {
            @Override
            public Future<Long> write(byte[] b) {
                try {
                    GenericModel model = JsonUtils.fromJson(new String(b), GenericModel.class);
                    System.out.println(gson.toJson(transformer.transform(model)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Futures.immediateFuture(0L);
            }

            @Override
            public void configure(Properties p) {

            }
        };
    }
}
