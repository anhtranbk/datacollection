package com.vcc.bigdata.transform;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.vcc.bigdata.collect.GraphCollectService;
import com.vcc.bigdata.collect.model.GraphModel;
import com.vcc.bigdata.collect.transform.OrgTransformer;
import com.datacollection.common.config.Configuration;
import com.datacollection.common.config.Properties;
import com.vcc.bigdata.extract.model.GenericModel;
import com.vcc.bigdata.platform.elastic.ElasticBulkInsert;
import com.vcc.bigdata.platform.mongo.MongoClientProvider;
import com.vcc.bigdata.platform.mongo.MongoConfig;
import org.bson.Document;
import org.elasticsearch.action.bulk.BulkResponse;

import java.util.concurrent.ExecutionException;

public class TestTransfomerMongo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        OrgTransformer tf = new OrgTransformer();
        String dbName = "datacollection";
        String collectionName = "orgs";
        Gson gson = new Gson();
        Properties props = new Configuration().toSubProperties("org");
        ElasticBulkInsert bulkInsert = new ElasticBulkInsert(props);
        int bulkSize = 500;
        GraphCollectService collectService = new GraphCollectService(props);

        MongoConfig mgConfig = new MongoConfig(props);
        MongoClient client = MongoClientProvider.getOrCreate("test", mgConfig);

        MongoCollection<Document> collection = client.getDatabase(dbName)
                .getCollection(collectionName);
        FindIterable<Document> docs = collection.find();
        int count = 0;
        for (Document doc : docs) {
            GenericModel genericModel = new GenericModel("", "", doc);
            GraphModel gm = tf.transform(genericModel);
//            count++;
//            System.out.println("doc solved:"+count);
//            collectService.collect(gm);
//            System.out.println(gson.toJson(gm));
            bulkInsert.addRequest("org", doc.getString("Title").toLowerCase(), gson.toJson(gm));
            if(bulkInsert.bulkSize()>bulkSize){
                BulkResponse bulkResponse = bulkInsert.submitBulk();
                System.out.println("Submit took:"+ bulkResponse.getTook());
            }
        }
    }
}