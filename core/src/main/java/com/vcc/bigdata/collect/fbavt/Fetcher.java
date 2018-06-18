package com.vcc.bigdata.collect.fbavt;

import com.vcc.bigdata.common.config.Configurable;
import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.common.utils.Reflects;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
interface Fetcher extends Configurable {

    /**
     * Fetch facebook avatar url from facebook cdn
     *
     * @param id per-app facebook id of user to retrieve avatar url
     * @return avatar url of user identified by id or null if not exist
     */
    String fetch(String id);

    static Fetcher create(Properties p) {
        Fetcher ins = Reflects.newInstance(p.getProperty("fbavatar.fetcher.class"));
        ins.configure(p);
        return ins;
    }
}
