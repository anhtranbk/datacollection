package com.datacollection.app.extractor.mongodb;

import com.datacollection.common.config.Configuration;
import com.datacollection.extract.EventType;
import com.datacollection.entity.Event;
import com.datacollection.extract.mongo.MongoExtractor;
import org.bson.Document;

public class FbPagePostExtractor extends MongoExtractor {

    public FbPagePostExtractor(Configuration config) {
        super("fbpage", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FB_FANPAGE_POST;

        document.put("Content", document.getString("Message"));
        document.put("_id", id);

        document.remove("Message");

        return new Event(id, type, document);
    }
}