package com.datacollection.extract.mongo;

import com.datacollection.common.config.Configuration;
import com.datacollection.extract.EventType;
import com.datacollection.entity.GenericModel;
import org.bson.Document;

public class ForumCommentExtractor extends MongoExtractor {

    public ForumCommentExtractor(Configuration config) {
        super("forum", config);
    }

    @Override
    protected GenericModel extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FORUM_COMMENT;

        document.put("_id", id);
        document.put("Content", document.getString("Comment"));
        document.remove("Comment");

        return new GenericModel(id, type, document);
    }
}
