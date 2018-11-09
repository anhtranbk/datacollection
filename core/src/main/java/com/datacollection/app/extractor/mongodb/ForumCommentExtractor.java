package com.datacollection.app.extractor.mongodb;

import com.datacollection.common.config.Configuration;
import com.datacollection.app.extractor.EventType;
import com.datacollection.entity.Event;
import com.datacollection.extract.mongo.MongoExtractor;
import org.bson.Document;

public class ForumCommentExtractor extends MongoExtractor {

    public ForumCommentExtractor(Configuration config) {
        super("forum", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FORUM_COMMENT;

        document.put("_id", id);
        document.put("Content", document.getString("Comment"));
        document.remove("Comment");

        return new Event(id, type, document);
    }
}
