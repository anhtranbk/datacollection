package com.datacollection.collect;

import com.datacollection.common.config.Configuration;
import com.datacollection.common.config.Properties;
import com.datacollection.common.broker.Record;
import com.datacollection.common.broker.Records;
import com.datacollection.common.utils.Threads;
import com.datacollection.entity.Event;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimpleCollector extends Collector {

    public SimpleCollector(Properties props) {
        super(props);
    }

    @Override
    public void handle(Records records) {
        for (Record record : records) {
            while (isNotCanceled()) {
                try {
                    Event event = getDeserializer().deserialize(record.data());
                    if (event != null) getService().collect(event).get(60, TimeUnit.SECONDS);
                    break;
                } catch (IOException e) {
                    logger.warn("Deserialize record error", e);
                } catch (Exception e) {
                    logger.error("Process record error: " + new String(record.data()), e);
                    Threads.sleep(TimeUnit.SECONDS.toMillis(5));
                }
            }
            counter.inc();
        }
    }

    public static void main(String[] args) {
        Properties p = new Configuration().toSubProperties("collect", "SimpleCollector");
        new SimpleCollector(p).start();
    }
}
