package com.vcc.bigdata.transform;

import com.google.gson.Gson;
import com.datacollection.app.collector.model.GraphModel;
import com.datacollection.collect.transform.ZambaTransformer;
import com.datacollection.common.config.Configuration;
import com.datacollection.serde.Deserializer;
import com.datacollection.serde.Serialization;
import com.datacollection.entity.Event;
import com.datacollection.platform.elastic.ElasticBulkInsert;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.bulk.BulkResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class TestTransformKafka {

    private static final ZambaTransformer dmpTransformer;
    private static final ElasticBulkInsert bulkInsert;

    static {
        dmpTransformer = new ZambaTransformer();
        Configuration config = new Configuration();
        bulkInsert = new ElasticBulkInsert(config);
    }

    public static void test(){
        int bulkSize = 500;
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.23.64:9092,192.168.23.65:9092,192.168.23.66:9092");
        props.put("group.id", "test20");
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        String topic= "zamba";

        consumer.subscribe(Arrays.asList(topic));
        System.out.println("Subscribed to topic " + topic);
        Gson gson = new Gson();
        Deserializer<Event> serializer = Serialization.create("json", Event.class).deserializer();
        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(0);
            for (ConsumerRecord<String, byte[]> record : records) {
                try {
                    Event generic = serializer.deserialize(record.value());
                    GraphModel graphModel = dmpTransformer.transform(generic);
//                    System.out.println(gson.toJson(graphModel));
//                    System.out.println(graphModel.profiles().get(0).type());
//                    Utils.systemError("stop");
                    bulkInsert.addRequest("graph", null, gson.toJson(graphModel));
                    if(bulkInsert.bulkSize()>bulkSize){
                        BulkResponse bulkResponse = bulkInsert.submitBulk();
                        System.out.println("Submit took:"+ bulkResponse.getTook());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        test();
    }
}

