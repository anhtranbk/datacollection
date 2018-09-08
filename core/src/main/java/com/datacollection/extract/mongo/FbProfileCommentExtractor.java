package com.datacollection.extract.mongo;

import com.datacollection.extract.EventType;
import com.mongodb.BasicDBObject;
import com.datacollection.common.config.Configuration;
import com.datacollection.entity.Event;
import org.bson.Document;

/**
 * Created by caoquy on 09/02/2017.
 */
public class FbProfileCommentExtractor extends MongoExtractor {

    public FbProfileCommentExtractor(Configuration config) {
        super("fbprofile", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FB_PROFILE_COMMENT;

        Document postDoc = database.getCollection("fbposts")
                .find(new BasicDBObject("_id", document.getObjectId("postId")))
                .first();
        if (postDoc != null) {
            document.put("userPostUrl", postDoc.getString("userPostUrl"));
            document.put("userPost", postDoc.getString("userPost"));
            document.put("postFbId", postDoc.getString("postFbId"));
            document.put("postType", postDoc.getString("postType"));
        }

        document.put("Content", document.getString("comment"));
        document.put("_id", id);

        document.remove("comment");

        return new Event(id, type, document);
    }
}
