package com.vcc.bigdata.collect;

import com.vcc.bigdata.common.config.Configuration;
import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.common.mb.Record;
import com.vcc.bigdata.common.mb.Records;
import com.vcc.bigdata.common.utils.Threads;
import com.vcc.bigdata.extract.model.GenericModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SimpleCollector extends Collector {

    public SimpleCollector(Properties props) {
        super(props);
    }

    @Override
    public void handle(Records records) {
        for (Record record : records) {
            while (isNotCanceled()) {
                try {
                    GenericModel generic = getDeserializer().deserialize(record.data());
                    if (generic != null) getService().collect(generic).get(60, TimeUnit.SECONDS);
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
