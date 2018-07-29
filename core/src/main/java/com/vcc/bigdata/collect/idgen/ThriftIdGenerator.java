package com.vcc.bigdata.collect.idgen;

import com.datacollection.common.config.Properties;
import com.datacollection.platform.thrift.ThriftClient;
import com.datacollection.platform.thrift.ThriftClientProvider;
import com.datacollection.platform.thrift.ThriftRuntimeException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import thriftgen.IdGenerator;

import java.util.List;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ThriftIdGenerator implements RemoteIdGenerator {

    private Properties props;
    private ThreadLocal<IdGenerator.Client> clientThreadLocal = new ThreadLocal<>();

    @Override
    public void configure(Properties p) {
        this.props = p;
    }

    @Override
    public long generate(List<String> seeds, long defVal) {
        try {
            if (seeds.isEmpty()) return defVal;
            IdGenerator.Client client = clientThreadLocal.get();
            if (client == null) {
                client = newClient(props);
                clientThreadLocal.set(client);
            }
            return client.generate(seeds, defVal);
        } catch (TException e) {
            throw new ThriftRuntimeException(e);
        }
    }

    private static IdGenerator.Client newClient(Properties props) {
        ThriftClient thriftClient = ThriftClientProvider.getOrCreate(Thread.currentThread().getName(), props);
        return new IdGenerator.Client(
                new TMultiplexedProtocol(thriftClient.getProtocol(), "IdGenerator"));
    }
}
