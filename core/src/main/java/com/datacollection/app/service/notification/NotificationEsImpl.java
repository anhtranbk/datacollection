package com.datacollection.app.service.notification;

import com.google.common.util.concurrent.ListenableFuture;
import com.datacollection.common.concurrenct.FutureAdapter;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.collect.IterableAdapter;
import com.datacollection.common.collect.Maps;
import com.datacollection.platform.elastic.ElasticClientProvider;
import com.datacollection.platform.elastic.ElasticConfig;
import com.datacollection.platform.elastic.EsScrollIterator;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class NotificationEsImpl implements NotificationService {

    private final Client client;
    private final String esIndex;
    private final int esScrollTimeoutInMinutes;
    private final int esScrollSize;

    public NotificationEsImpl(Configuration conf) {
        conf = conf.getSubConfiguration("notification");
        ElasticConfig esConfig = new ElasticConfig(conf);

        this.client = ElasticClientProvider.getDefault(esConfig);
        this.esIndex = esConfig.getElasticIndex();
        this.esScrollTimeoutInMinutes = conf.getInt("es.scroll.timeout.minutes", 5);
        this.esScrollSize = conf.getInt("es.scroll.size", 500);
    }

    @Override
    public ListenableFuture<Iterable<Message>> addMessages(Collection<Message> messages) {
        BulkRequestBuilder requestBuilder = client.prepareBulk();
        messages.forEach(msg -> {
            IndexRequest req = new IndexRequest(esIndex, msg.getType(), msg.getKey());
            req.source(msg.getProperties());
            requestBuilder.add(req);
        });
        return FutureAdapter.from(requestBuilder.execute(), resp -> messages);
    }

    @Override
    public ListenableFuture<Message> removeMessage(Message msg) {
        DeleteRequestBuilder requestBuilder = client.prepareDelete(esIndex, msg.getType(), msg.getKey());
//        requestBuilder.setVersion(msg.getVersion());
        return FutureAdapter.from(requestBuilder.execute(), resp -> msg);
    }

    @Override
    public Iterable<Message> getMessages(String type) {
        SearchResponse searchResp = client.prepareSearch(esIndex).setTypes(type)
                .setQuery(QueryBuilders.matchAllQuery())
                .setVersion(true)
                .setSize(esScrollSize)
                .setScroll(new TimeValue(esScrollTimeoutInMinutes, TimeUnit.MINUTES))
                .execute().actionGet();

        Iterator<SearchHit> hitIterator = new EsScrollIterator(client, searchResp, esScrollTimeoutInMinutes);
        return new IterableAdapter<SearchHit, Message>(hitIterator) {
            @Override
            protected Message convert(SearchHit hit) {
                Message msg = new Message(hit.getType(), hit.getId());
                msg.setVersion(hit.getVersion());
                msg.putProperties(Maps.convertToTextMap(hit.getSource()));
                return msg;
            }
        };
    }

    @Override
    public void close() {
        this.client.close();
    }

    @Override
    public void flush() {
        this.client.admin().indices().prepareRefresh(esIndex);
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        NotificationService notificationService = NotificationService.create(conf);

        Message msg = new Message("phone", "fb.com", "0973328004", "235418957128571290");
        msg.setVersion(1);
//        msg.putProperty("source", "fb.com");
//        msg.putProperty("value", "0973328004");
//        msg.putProperty("uid", "235418957128571290");
//        notiService.addMessage(msg).get();

        for (Message message : notificationService.getMessages("phone")) {
            System.out.println(message.getProperties());
            notificationService.removeMessage(message).get();
        }

        notificationService.flush();
    }
}
