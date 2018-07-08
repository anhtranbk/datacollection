#!/usr/bin/env bash

#export JVM_OPTS="-Xms2G -Xmx2G"

$(dirname $0)/run-class.sh --class com.vcc.bigdata.collect.WalCollector "$@"