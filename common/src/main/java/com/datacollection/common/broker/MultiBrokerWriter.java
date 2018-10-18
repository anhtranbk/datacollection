package com.datacollection.common.broker;

import com.datacollection.common.concurrenct.FutureAdapter;
import com.datacollection.common.config.Properties;
import com.datacollection.common.utils.Reflects;
import com.datacollection.common.concurrenct.AllInOneFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

public class MultiBrokerWriter implements BrokerWriter {

    public List<BrokerWriter> brokerWriters = new ArrayList<>();

    public MultiBrokerWriter(BrokerWriter... brokerWriters) {
        this.brokerWriters.addAll(Arrays.asList(brokerWriters));
    }

    @Override
    public Future<Long> write(byte[] b) {
        List<Future<Long>> futures = new ArrayList<>();
        for (BrokerWriter brokerWriter : brokerWriters) {
            futures.add(brokerWriter.write(b));
        }

        Future<List<Long>> fut = AllInOneFuture.from(futures);
        return FutureAdapter.from(fut, list -> (long) list.size());
    }

    @Override
    public void configure(Properties p) {
        List<String> factoryClasses = p.getCollection("mb.multi.factory.classes");
        factoryClasses.forEach(factory -> {
            BrokerFactory brokerFactory = Reflects.newInstance(factory);
            BrokerWriter brokerWriter = brokerFactory.getWriter();
            brokerWriter.configure(p);
            this.brokerWriters.add(brokerWriter);
        });
    }

    public List<BrokerWriter> getMsgBrokerWriters() {
        return brokerWriters;
    }

    public void setMsgBrokerWriters(List<BrokerWriter> brokerWriters) {
        this.brokerWriters = brokerWriters;
    }
}
