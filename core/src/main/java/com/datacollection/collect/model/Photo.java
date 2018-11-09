package com.datacollection.collect.model;

import com.datacollection.collect.Constants;
import com.datacollection.common.utils.Hashings;
import com.datacollection.common.collect.Maps;
import com.datacollection.app.service.FacebookClient;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Photo extends BaseEntity {

    public Photo(String url) {
        this(url, Collections.emptyMap());
    }

    public Photo(String url, Object... keyValues) {
        this(url, Maps.initFromKeyValues(keyValues));
    }

    public Photo(String url, Map<String, Object> properties) {
        super("", Constants.PHOTO, properties);
        this.putProperty("url", url);
        this.putProperty("_ts", System.currentTimeMillis());

        String domain = properties.get("domain").toString();
        String id = Constants.FACEBOOK.equals(domain) ? extractIdentifiedInfo(url) : url;
        this.setId(Hashings.sha1AsBase64(id, false));
    }

    private static String extractIdentifiedInfo(String originUrl) {
        try {
            int end = originUrl.indexOf("?");
            String shortUrl = originUrl.substring(0, end);
            return shortUrl.substring(shortUrl.lastIndexOf("/") + 1, end);
        } catch (Exception e) {
            return originUrl;
        }
    }

    public static void main(String[] args) throws IOException {
        int[] size = new int[]{24, 32, 40, 60, 80, 100, 160, 200, 240, 480, 720, 7200};
        for (int s : size) {
            String url = FacebookClient.fetchAvatarUrl("1299092363551733", s, s);
            System.out.println(s + ": " + extractIdentifiedInfo(url));
        }
    }
}
