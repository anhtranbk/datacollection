package com.vcc.bigdata.collect.fbavt;

import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.service.FacebookClient;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class DirectHttpFetcher implements Fetcher {

    @Override
    public void configure(Properties p) {
    }

    @Override
    public String fetch(String id) {
        try {
            return FacebookClient.fetchAvatarUrl(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}