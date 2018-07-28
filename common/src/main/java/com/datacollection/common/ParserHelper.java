package com.datacollection.common;

import com.datacollection.common.utils.NullProtector;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ParserHelper {

    public static String parseForumId(String url) {
        String[] split = url.split("/");
        return split.length < 4 ? null : split[split.length - 1];
    }

    public static String parseDomain(String url) {
        String prefix = url.startsWith("https://") ? "https://" : "http://";
        int end = url.indexOf("/", prefix.length() + 1);
        if (end == -1) {
            return url.substring(prefix.length());
        } else {
            String domain = url.substring(prefix.length(), url.indexOf("/", prefix.length() + 1));
            String[] split = domain.split("\\.");
            int len = split.length;
            if (len > 2) {
                domain = split[len - 2] + "." + split[len - 1];
            }

            return domain;
        }
    }

    public static String parsePageId(String postId) {
        return NullProtector.get(postId.split("_"), 0).orElse(null);
    }
}
