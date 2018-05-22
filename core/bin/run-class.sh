#!/usr/bin/env bash

base_dir=$(dirname $0)/..

TMP_DIR=${base_dir}/tmp

LOG4J_OPTS=${base_dir}/config/log4j.xml
HBASE_OPTS=${base_dir}/config/hbase-site.xml
APP_CONFIG_OPTS=${base_dir}/config/local.properties
HYSTRIX_CONFIG_OPTS="file://"${base_dir}/config/hystrix.properties

KAFKA_PRODUCER_OPTS=${base_dir}/config/kafka-producer.properties
KAFKA_CONSUMER_OPTS=${base_dir}/config/kafka-consumer.properties

if [ -z "$JVM_OPTS" ]; then
  JVM_OPTS="-Xms512M -Xmx512M"
fi

CLASSPATH=./*
[ -d "target" ] && CLASSPATH=${CLASSPATH}:"target/*"
[ -d "target/lib" ] && CLASSPATH=${CLASSPATH}:"target/lib/*"
[ -d "lib" ] && CLASSPATH=${CLASSPATH}:"lib/*"

java -Dlog4j.configuration="file:"${LOG4J_OPTS} \
    -Dkafka.producer.conf=${KAFKA_PRODUCER_OPTS} \
    -Dkafka.consumer.conf=${KAFKA_CONSUMER_OPTS} \
    -Dhbase.conf=${HBASE_OPTS} \
    -Darchaius.configurationSource.additionalUrls=${HYSTRIX_CONFIG_OPTS} \
    -Djava.io.tmpdir=${TMP_DIR} \
    -Dapp.conf=${APP_CONFIG_OPTS} ${JVM_OPTS} -cp ${CLASSPATH} \
     com.vcc.bigdata.Main "$@"