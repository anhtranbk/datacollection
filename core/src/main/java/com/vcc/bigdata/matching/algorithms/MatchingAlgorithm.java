package com.vcc.bigdata.matching.algorithms;

import com.vcc.bigdata.matching.ProfileLog;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by kumin on 30/11/2017.
 */
public interface MatchingAlgorithm {
    Map<String, Double> computeScore(Collection<ProfileLog> profileLogs, String value);
    List<String> getUidMaxScore(Map<String, Double> uidScore);
}
