package com.datacollection.app.extractor.mongodb;

import com.datacollection.common.config.Configuration;
import com.datacollection.extract.EventType;
import com.datacollection.entity.Event;
import com.datacollection.extract.mongo.MongoExtractor;
import org.bson.Document;

/**
 * Created by kumin on 24/11/2017.
 */
public class FbProfile2Extractor extends MongoExtractor {

    public FbProfile2Extractor(Configuration config) {
        super("fbprofile", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FB_PROFILE_NEW;

        document.put("_id", id);
        return new Event(id, type, document);
    }
}
