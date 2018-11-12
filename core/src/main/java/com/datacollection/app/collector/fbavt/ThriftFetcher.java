package com.datacollection.app.collector.fbavt;

import com.datacollection.common.config.Properties;
import com.datacollection.platform.thrift.ThriftClient;
import com.datacollection.platform.thrift.ThriftClientProvider;
import com.datacollection.platform.thrift.ThriftRuntimeException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import thriftgen.FbAvatarService;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ThriftFetcher implements Fetcher {

    private Properties props;
    private ThreadLocal<FbAvatarService.Client> clientThreadLocal = new ThreadLocal<>();

    @Override
    public void configure(Properties p) {
        this.props = p;
    }

    @Override
    public String fetch(String id) {
        try {
            FbAvatarService.Client client = clientThreadLocal.get();
            if (client == null) {
                client = newClient(props);
                clientThreadLocal.set(client);
            }
            return client.fetchAvatarUrl(id);
        } catch (TException e) {
            throw new ThriftRuntimeException(e);
        }
    }

    private static FbAvatarService.Client newClient(Properties props) {
        ThriftClient thriftClient = ThriftClientProvider.getOrCreate(Thread.currentThread().getName(), props);
        return new FbAvatarService.Client(
                new TMultiplexedProtocol(thriftClient.getProtocol(), "FbAvatarService"));
    }
}
