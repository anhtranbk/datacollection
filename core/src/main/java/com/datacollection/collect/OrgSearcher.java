package com.datacollection.collect;

import com.datacollection.common.config.Configuration;
import com.datacollection.platform.elastic.ElasticClientProvider;
import com.datacollection.platform.elastic.ElasticConfig;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrgSearcher {
    private Client client;
    private static final String index = "datacollection-org";
    private static final String type = "org";

    public OrgSearcher() {
        ElasticConfig elasticConfig = new ElasticConfig(new Configuration());
        client = ElasticClientProvider.getOrCreate("profile2 transformer", elasticConfig);
    }

    public List<String> matchOrg(String orgName) {
        List<String> orgs = new ArrayList<>();
        try {

            SearchResponse sr = client.prepareSearch(index).
                    setTypes(type).
                    setQuery(QueryBuilders.regexpQuery("post.Title", ".*"+orgName+".*")).
                    execute().actionGet();

            for (SearchHit hit : sr.getHits()) {
                String title = ((Map<String, String>) hit.getSource().get("post")).get("Title").toLowerCase();
                orgs.add(title);
            }
        } catch (ElasticsearchException ignore) {
//            System.out.println(e.getMessage());
        }
        return orgs;
    }
}
