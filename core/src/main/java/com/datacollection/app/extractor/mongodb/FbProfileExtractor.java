package com.datacollection.app.extractor.mongodb;

import com.datacollection.common.config.Configuration;
import com.datacollection.app.extractor.EventType;
import com.datacollection.entity.Event;
import com.datacollection.extract.mongo.MongoExtractor;
import org.bson.Document;

/**
 * Created by kumin on 23/05/2017.
 */
public class FbProfileExtractor extends MongoExtractor {

    public FbProfileExtractor(Configuration config) {
        super("fbprofile", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FB_PROFILE;

        document.put("_id", id);

        document.remove("fetchFriends");
        document.remove("isBackUp");
        document.remove("text");

        return new Event(id, type, document);
    }
}
