package com.datacollection.extract.elastic;

import com.datacollection.common.config.Configuration;
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

import java.util.LinkedHashMap;
import java.util.Map;

/*
@author - kumin
 */
public class DmpExtractor extends Extractor {

    private Client client;
    private String eIndex;

    public DmpExtractor(Configuration config) {
        super("guid", config);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        ElasticConfig elasticConfig = new ElasticConfig(props);
        eIndex = elasticConfig.getElasticIndex();
        client = ElasticClientProvider.getOrCreate("dmp", elasticConfig);
    }

    @Override
    protected void onLoop() throws Exception {
        SearchResponse scroll = client.prepareSearch(eIndex.split(","))
                .setTypes("info")
                .setSize(1000)
                .setScroll(new TimeValue(6000000))
                .get();

        while (isNotCanceled()) {

            for (SearchHit hit : scroll.getHits()) {
                sendEvent(createEvent(hit));
            }
            scroll = client.prepareSearchScroll(scroll.getScrollId()).setScroll(new TimeValue(6000000))
                    .execute().actionGet();

            if (scroll.getHits().getTotalHits() == 0) break;
        }
    }

    private Event createEvent(SearchHit hit) {
        Map<String, Object> source = hit.getSource();
        Map<String, Object> post = new LinkedHashMap<>();
        post.put("domain", "dmp");
        if (source.get("phones") != null) {
            post.put("phones", source.get("phones"));
        }
        if (source.get("emails") != null) {
            post.put("emails", source.get("emails"));
        }
        if (source.get("idSrc") != null) {
            post.put("viet_id", source.get("idSrc"));
        }

        String guid = source.get("id").toString();
        post.put("guid", guid);
        SearchResponse sr = client.prepareSearch(eIndex.split(","))
                .setTypes("demographics")
                .setQuery(QueryBuilders.matchPhraseQuery("id", guid))
                .get();

        if (sr.getHits().totalHits() > 0) {
            SearchHit dgHit = sr.getHits().getAt(0);
            if (dgHit.getSource().get("gender") != null) {
                post.put("gender", this.getGender(Integer.parseInt(dgHit.getSource().get("gender").toString())));
            }
            if (dgHit.getSource().get("age") != null) {
                post.put("age", this.getAge(Integer.parseInt(dgHit.getSource().get("age").toString())));
            }
        }
        return new Event(guid, EventType.TYPE_DMP, post);
    }


    public String getGender(int no) {
        switch (no) {
            case 1:
                return "m";
            case 2:
                return "f";
        }
        return null;
    }

    public String getAge(int no) {
        switch (no) {
            case 3:
                return "<18";
            case 4:
                return "18 -> 24";
            case 5:
                return "25 -> 34";
            case 6:
                return "35 -> 50";
            case 7:
                return ">50";
        }
        return null;
    }

    public String getInterest(int no) {
        return null;
    }
}
