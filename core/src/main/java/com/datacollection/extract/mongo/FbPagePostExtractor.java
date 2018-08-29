package com.datacollection.extract.mongo;

import com.datacollection.common.config.Configuration;
import com.datacollection.extract.EventType;
import com.datacollection.extract.model.GenericModel;
import org.bson.Document;

public class FbPagePostExtractor extends MongoExtractor {

    public FbPagePostExtractor(Configuration config) {
        super("fbpage", config);
    }

    @Override
    protected GenericModel extractData(Document document) {
        String id = document.getObjectId("_id").toHexString();
        String type = EventType.TYPE_FB_FANPAGE_POST;

        document.put("Content", document.getString("Message"));
        document.put("_id", id);

        document.remove("Message");

        return new GenericModel(id, type, document);
    }
}
