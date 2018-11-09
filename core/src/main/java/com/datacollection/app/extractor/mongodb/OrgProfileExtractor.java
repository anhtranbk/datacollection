package com.datacollection.app.extractor.mongodb;

import com.datacollection.app.extractor.EventType;
import com.datacollection.extract.mongo.MongoDataStream;
import com.datacollection.extract.mongo.MongoExtractor;
import com.datacollection.extract.mongo.MongoFetcher;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.broker.MockBrokerFactory;
import com.datacollection.extract.DataStream;
import com.datacollection.extract.Extractor;
import com.datacollection.entity.Event;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrgProfileExtractor extends MongoExtractor {

    private static final long MIN_EPOCH = 1262278800000L;
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OrgProfileExtractor(Configuration config) {
        super("org", config);
    }

    @Override
    protected Event extractData(Document document) {
        String id = document.getString("_id");
        String type = EventType.TYPE_ORG;

        document.put("_id", id);
        return new Event(id, type, document);
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
                        .find(Filters.gt("GetDate", fromIndex))
                        .sort(new BasicDBObject("GetDate", 1))
                        .limit(batchSize)
                        .iterator();
            }

            @Override
            public Object fetchIndex(Document doc) {
                return doc.getDate("GetDate");
            }
        }, lastIndex);
    }

    @Override
    protected void onEventProcessed(Event event, long queueOrder, Object attachment) {
        Document doc = (Document) attachment;
        storeIndex(df.format(doc.getDate("GetDate")), queueOrder);
    }

    public static void main(String[] args) {
        Extractor extractor = new OrgProfileExtractor(new Configuration());
        extractor.setBrokerFactory(new MockBrokerFactory());
        extractor.start();
    }
}