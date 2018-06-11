package com.vcc.bigdata.collect.transform;

import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.common.mb.MsgBrokerFactory;
import com.vcc.bigdata.common.mb.MsgBrokerReader;
import com.vcc.bigdata.common.mb.MsgBrokerWriter;
import com.vcc.bigdata.common.utils.Utils;
import com.vcc.bigdata.extract.model.GenericModel;

import java.util.concurrent.Future;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class TransformTester implements MsgBrokerFactory {

    private final Gson gson = new Gson();
    private final DataTransformer transformer;

    public TransformTester(DataTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    public MsgBrokerReader createReader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MsgBrokerWriter createWriter() {
        return new MsgBrokerWriter() {
            @Override
            public Future<Long> write(byte[] b) {
                try {
                    GenericModel model = Utils.fromJson(new String(b), GenericModel.class);
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
