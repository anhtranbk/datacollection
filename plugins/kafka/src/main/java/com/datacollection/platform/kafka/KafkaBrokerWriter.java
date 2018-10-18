package com.datacollection.platform.kafka;

import com.datacollection.common.concurrenct.FutureAdapter;
import com.datacollection.common.config.Properties;
import com.datacollection.common.broker.BrokerWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Level;

import java.util.concurrent.Future;

public class KafkaBrokerWriter implements BrokerWriter {

    static final String KEY_KAFKA_TOPIC = "kafka.producer.topic";

    private String topic;
    private Producer<String, byte[]> producer;

    public KafkaBrokerWriter(Properties props) {
        this.configure(props);
    }

    public KafkaBrokerWriter() {
    }

    @Override
    public void configure(Properties p) {
        this.topic = p.getProperty(KEY_KAFKA_TOPIC, "test");
        this.producer = new KafkaProducer<>(KafkaConfig.producerProperties());
    }

    @Override
    public Future<Long> write(byte[] b) {
        return FutureAdapter.from(producer.send(new ProducerRecord<>(topic, b)), RecordMetadata::offset);
    }

    @Override
    public void flush() {
        this.producer.flush();
    }

    @Override
    public void close() {
        this.producer.close();
    }

    static {
        org.apache.log4j.Logger.getLogger("org").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("akka").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("kafka").setLevel(Level.WARN);
    }
}
