package com.datacollection.platform.kafka;

import com.datacollection.common.config.Properties;
import com.datacollection.common.broker.AbstractBrokerReader;
import com.datacollection.common.broker.Record;
import com.datacollection.common.broker.Records;
import com.datacollection.common.utils.Strings;
import com.datacollection.common.utils.Threads;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaBrokerReader extends AbstractBrokerReader {

    static final String KEY_MIN_RECORDS = "kafka.min.records";
    static final String KEY_TOPICS = "kafka.consumer.topics";
    static final String KEY_NUM_CONSUMERS = "kafka.num.consumers";

    private static final Logger logger = LoggerFactory.getLogger(KafkaBrokerReader.class);
    private Collection<String> topics;
    private int numConsumers;
    // min number records to be retrieved before sending to handlers
    private int minRecords;
    private ExecutorService executor;
    private Properties consumerProps;

    public KafkaBrokerReader(Properties p) {
        this.configure(p);
    }

    public KafkaBrokerReader(Collection<String> topics, int numConsumers, int minRecords,
                             ExecutorService executor, Properties consumerProps) {
        this.topics = topics;
        this.numConsumers = numConsumers;
        this.minRecords = minRecords;
        this.executor = executor;
        this.consumerProps = consumerProps;
    }

    public KafkaBrokerReader() {
    }

    @Override
    public void configure(Properties props) {
        super.configure(props);
        this.consumerProps = KafkaConfig.consumerProperties();
        this.topics = props.getCollection(KEY_TOPICS);
        this.minRecords = props.getInt(KEY_MIN_RECORDS, 100);

        this.numConsumers = props.getInt(KEY_NUM_CONSUMERS, Runtime.getRuntime().availableProcessors());
        // number executor threads same as number consumers
        this.executor = Executors.newFixedThreadPool(numConsumers);
    }

    @Override
    public void doStart() {
        for (int i = 0; i < numConsumers; i++) {
            executor.submit(() -> startKafkaConsumer(topics));
        }
    }

    @Override
    public void doStop() {
        logger.info("Stop Kafka queue reader");
        executor.shutdown();
    }

    protected void startKafkaConsumer(Collection<String> topics) {
        while (isRunning()) {
            try (KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(consumerProps)) {
                consumer.subscribe(topics);
                logger.info(Thread.currentThread().getName() + " start subscribe topic: "
                        + Strings.join(topics, ","));

                Records queueRecords = new Records();
                while (isRunning()) {
                    ConsumerRecords<String, byte[]> records = consumer.poll(100);
                    for (ConsumerRecord<String, byte[]> record : records) {
                        queueRecords.add(new Record(record.value(), record.timestamp()));
                    }
                    if (queueRecords.size() >= minRecords) {
                        invokeHandlers(queueRecords);
                        consumer.commitAsync();
                        queueRecords.clear();
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                Threads.sleep(5, TimeUnit.SECONDS);
            }
        }
        logger.info("Consumer stopped at thread: " + Thread.currentThread().getName());
    }

    static {
        org.apache.log4j.Logger.getLogger("org").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("akka").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("kafka").setLevel(Level.WARN);
    }
}
