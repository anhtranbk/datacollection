package com.datacollection.app.extractor.mongodb;

import com.datacollection.common.config.Configuration;
import com.datacollection.app.extractor.EventType;
import com.datacollection.entity.Event;
import com.datacollection.extract.mongo.MongoExtractor;
import org.bson.Document;

/**
 * Created by caoquy on 09/02/2017.
 */
public class FbProfilePostExtractor extends MongoExtractor {

    public FbProfilePostExtractor(Configuration config) {
        super("fbprofile", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FB_PROFILE_POST;

        document.put("Content", document.getString("postContent"));
        document.put("_id", id);

        document.remove("content");
        document.remove("postContent");

        return new Event(id, type, document);
    }
}
