package com.vcc.bigdata.jobs.fbavatar;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.vcc.bigdata.common.config.Configuration;
import com.vcc.bigdata.common.utils.Hashings;
import com.vcc.bigdata.common.utils.Utils;
import com.vcc.bigdata.metric.CounterMetrics;
import com.vcc.bigdata.metric.Counting;
import com.vcc.bigdata.metric.Sl4jPublisher;
import com.vcc.bigdata.platform.cassandra.CassandraClusterProvider;
import com.vcc.bigdata.platform.cassandra.CassandraConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CacheFbAvatarTest {

    public static void main(String[] args) {
        CassandraConfig conf = new CassandraConfig(new Configuration());
        Session session = CassandraClusterProvider.getDefault(conf).connect("datacollection");

        AtomicInteger counter = new AtomicInteger();
        CounterMetrics counterMetrics = new CounterMetrics(new Sl4jPublisher(), "test",
                "test", Counting.from(counter), 1000);
        counterMetrics.start();

        Map<Long, byte[]> map = new HashMap<>();
        AtomicBoolean canceled = new AtomicBoolean(false);
        Utils.addShutdownHook(() -> canceled.set(true));

        for (Row row : session.execute("SELECT id, url FROM fbavatar")) {
            try {
                long key = Long.parseLong(row.getString("id"));
                byte[] value = Hashings.sha1(row.getString("url"));
                map.put(key, value);
                counter.incrementAndGet();
            } catch (RuntimeException ignored) {
            }
            if (canceled.get()) break;
        }

        System.out.println("Map size: " + map.size());
    }
}
