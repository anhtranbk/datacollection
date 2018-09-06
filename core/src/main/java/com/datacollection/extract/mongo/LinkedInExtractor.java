package com.datacollection.extract.mongo;

import com.datacollection.extract.EventType;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.broker.MockBrokerFactory;
import com.datacollection.extract.DataStream;
import com.datacollection.entity.GenericModel;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LinkedInExtractor extends MongoExtractor {
    static final long MIN_EPOCH = 1262278800000L;
    static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LinkedInExtractor(Configuration config) {
        super("linkedin", config);
    }

    @Override
    protected GenericModel extractData(Document document) {
        String id = document.getString("_id");
        String type = EventType.TYPE_LINKEDIN;
        return new GenericModel(id, type, document);
    }

    @Override
    protected DataStream<Document> openDataStream() {
        String strIndex = loadIndex();
        Date lastIndex;
        try {
            lastIndex = df.parse(strIndex);
        } catch (Exception e) {
            lastIndex = new Date(MIN_EPOCH);
        }
        logger.info("Last index: " + df.format(lastIndex));

        return new MongoDataStream(new MongoFetcher() {
            @Override
            public MongoCursor<Document> fetchNextDocs(Object fromIndex) {
                return database.getCollection(collection)
                        .find(Filters.gt("PostDate", fromIndex))
                        .sort(new BasicDBObject("PostDate", 1))
                        .limit(batchSize)
                        .iterator();
            }

            @Override
            public Object fetchIndex(Document doc) {
                return doc.getDate("PostDate");
            }
        }, lastIndex);
    }

    @Override
    protected void onRecordProcessed(GenericModel model, long queueOrder, Object attachment) {
        Document doc = (Document) attachment;
        storeIndex(df.format(doc.getDate("PostDate")), queueOrder);
    }

    public static void main (String [] args){
        LinkedInExtractor linkedInExtractor = new LinkedInExtractor(new Configuration());
        linkedInExtractor.setMsgBrokerFactory(new MockBrokerFactory());
        linkedInExtractor.start();
    }

}
