package com.datacollection.extract.elastic;

import com.datacollection.common.config.Configuration;
import com.datacollection.common.broker.MockBrokerFactory;
import com.datacollection.common.utils.Threads;
import com.datacollection.extract.EventType;
import com.datacollection.extract.Extractor;
import com.datacollection.entity.Event;
import com.datacollection.platform.elastic.ElasticClientProvider;
import com.datacollection.platform.elastic.ElasticConfig;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EcommerceExtractor extends Extractor {

    private Client client;
    private final int esScrollSize;
    private final String esIndex;
    private final int esScrollTimeout;

    public EcommerceExtractor(Configuration config) {
        super("ecommerce", config);
        esIndex = props.getProperty("es.index.name", "ecommerce");
        esScrollSize = props.getInt("es.scroll.size", 1000);
        esScrollTimeout = props.getInt("es.scroll.timeout.minutes", 5);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        client = ElasticClientProvider.getDefault(new ElasticConfig(props));
    }

    @Override
    protected void onLoop() {
        int defSkip = props.getInt("es.skip", 0);
        int skip = Integer.parseInt(loadIndex(String.valueOf(defSkip)));
        logger.info("Skip " + skip + " documents");
        Threads.sleep(1000);

        SearchResponse scrollResp = client.prepareSearch(esIndex).setTypes("profile")
                .setQuery(QueryBuilders.matchAllQuery())
//                .setQuery(QueryBuilders.termQuery("source_name", "enbac"))
                .setSize(esScrollSize)
                .setScroll(new TimeValue(esScrollTimeout, TimeUnit.MINUTES))
                .execute().actionGet();
        long totalPost = scrollResp.getHits().getTotalHits();
        logger.info("Total post: " + totalPost);

        int count = 0;
        while (isNotCanceled()) {
            for (SearchHit hit : scrollResp.getHits()) {
                if (count++ < skip) continue;
                final int c = count;

                // send current count value as attachment to keep in index
                sendEvent(createEvent(hit), c);
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(esScrollTimeout, TimeUnit.MINUTES))
                    .execute().actionGet();

            //Break condition: No hits are returned
            if (scrollResp.getHits().getHits().length == 0) break;
        }

        // ES ecommerce extractor do not need loop
        this.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.close();
    }

    @Override
    protected void onEventProcessed(Event event, long queueOrder, Object attachment) {
        storeIndex(attachment.toString(), queueOrder);
    }

    private Event createEvent(SearchHit hit) {
        Map<String, Object> source = hit.getSource();
        String id = hit.getId();

        Event event = new Event();
        event.setType(EventType.TYPE_ECOMMERCE);
        event.setProperty("url", "es://ecommerce/profile/" + id);
        event.putProperties(source);

        return event;
    }

    public static void main(String[] args) {
        Configuration conf = new Configuration();
        Extractor extractor = new EcommerceExtractor(conf);
        extractor.setBrokerFactory(new MockBrokerFactory());
        extractor.start();
    }
}
