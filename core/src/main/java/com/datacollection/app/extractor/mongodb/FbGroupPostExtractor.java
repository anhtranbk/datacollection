package com.datacollection.app.extractor.mongodb;

import com.datacollection.common.config.Configuration;
import com.datacollection.app.extractor.EventType;
import com.datacollection.entity.Event;
import com.datacollection.extract.mongo.MongoExtractor;
import org.bson.Document;

public class FbGroupPostExtractor extends MongoExtractor {

    public FbGroupPostExtractor(Configuration config) {
        super("fbgroup", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FB_GROUP_POST;

        document.put("Content", document.getString("Message"));
        document.put("_id", id);

        document.remove("Message");

        return new Event(id, type, document);
    }
}
