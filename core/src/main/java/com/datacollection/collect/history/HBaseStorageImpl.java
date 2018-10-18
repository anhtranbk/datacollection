package com.datacollection.collect.history;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.datacollection.collect.model.History;
import com.datacollection.common.concurrency.FutureAdapter;
import com.datacollection.common.config.Properties;
import com.datacollection.common.collect.IterableAdapter;
import com.datacollection.platform.hbase.AbstractRepository;
import com.datacollection.platform.hbase.HBaseRuntimeException;
import com.datacollection.platform.hbase.HBaseUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class HBaseStorageImpl extends AbstractRepository implements HistoryStorage {

    private final TableName tableLogs;

    public HBaseStorageImpl(Properties p) {
        super(p);
        this.tableLogs = TableName.valueOf(p.getProperty("history.hbase.table", "logs"));
    }

    @Override
    public ListenableFuture<History> addHistory(String uid, History history) {
        Future<?> fut = execute("AddLogs", () -> {
            try (Table table = connection.getTable(tableLogs)) {
                byte[] row = HBaseUtils.buildCompositeKeyWithBucket(
                        history.id(), // seed
                        uid.getBytes(),
                        history.id().getBytes());

                Put put = new Put(row);
                put.addColumn(CF, CQ_HIDDEN, HBaseUtils.EMPTY);
                history.properties().forEach((k, v)
                        -> put.addColumn(CF, k.getBytes(), v.toString().getBytes()));
                table.put(put);
            } catch (IOException e) {
                throw new HBaseRuntimeException(e);
            }
        });
        return FutureAdapter.from(fut, o -> history);
    }

    @Override
    public Iterable<History> findHistoryByUid(String uid) {
        try (Table table = connection.getTable(tableLogs)) {
            List<Iterable<History>> list = new ArrayList<>(HBaseUtils.DEFAULT_MAX_BUCKET);

            for (int i = 0; i < HBaseUtils.DEFAULT_MAX_BUCKET; i++) {
                Scan scan = new Scan();
                byte[] bucket = String.valueOf(i).getBytes();
                scan.setRowPrefixFilter(HBaseUtils.createCompositeKey(bucket, uid.getBytes()));

                ResultScanner scanner = table.getScanner(scan);
                Iterable<History> logs = IterableAdapter.from(scanner, result -> {
                    String source = Bytes.toString(result.getValue(CF, "src".getBytes()));
                    String url = Bytes.toString(result.getValue(CF, "url".getBytes()));
                    String type = Bytes.toString(result.getValue(CF, "type".getBytes()));

                    History history = new History(type, source, url);
                    result.getFamilyMap(CF).forEach((k, v)
                            -> history.putProperty(Bytes.toString(k), Bytes.toString(v)));

                    return history;
                });
                list.add(logs);
            }

            return Iterables.concat(list);
        } catch (IOException e) {
            throw new HBaseRuntimeException(e);
        }
    }
}
