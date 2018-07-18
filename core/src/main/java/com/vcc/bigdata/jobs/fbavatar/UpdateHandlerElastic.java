package com.vcc.bigdata.jobs.fbavatar;

import com.vcc.bigdata.collect.Constants;
import com.vcc.bigdata.collect.fbavt.FbAvatarService;
import com.vcc.bigdata.collect.model.Photo;
import com.vcc.bigdata.graphdb.GraphSession;
import com.vcc.bigdata.graphdb.Vertex;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UpdateHandlerElastic extends UpdateHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<String> fbIds;
    private Vertex profile;
    private Client elasticClient;
    private String elasticIndex;
    private FbAvatarService fbAvatarService;

    public UpdateHandlerElastic(List<String> fbIds,
                         Vertex profile,
                         int version,
                         Client elasticClient,
                         String elasticIndex,
                         GraphSession session,
                         FbAvatarService fbAvatarService) {
        super(version, session);
        this.fbIds = fbIds;
        this.profile = profile;
        this.fbAvatarService = fbAvatarService;
        this.elasticClient = elasticClient;
        this.elasticIndex = elasticIndex;
    }

    @Override
    public void run() {
        try {
            for (String fbId : fbIds) {
                String avatarUrl = fbAvatarService.getAvatarUrl(fbId);
                if (avatarUrl == null) continue;
                Photo newPhoto = new Photo(avatarUrl, "domain", Constants.FACEBOOK,
                        "_ts", System.currentTimeMillis(), "newest", 1);
                String photoProfile = checkAvatarExist(avatarUrl);
                if (photoProfile != null) {
                    Vertex photoProfileVetex = Vertex.create(photoProfile, "profile");
                    updateAvatarExist(profile, photoProfileVetex, newPhoto);
                } else {
                    updateAvatarNotExist(profile, newPhoto);
                }
                logger.info("Update Avatar Is Success!");
                break;
            }
        } catch (Throwable ignore) {

        }
    }

    public String checkAvatarExist(String url) {
        SearchResponse sr = elasticClient.prepareSearch(elasticIndex)
                .setTypes("profiles")
                .setQuery(QueryBuilders.termQuery("photo.url", url))
                .setSize(1000)
                .execute()
                .actionGet();
        if (sr.getHits().getTotalHits() == 0) return null;
        SearchHit hit = sr.getHits().getAt(0);
        return hit.getId();
    }


}