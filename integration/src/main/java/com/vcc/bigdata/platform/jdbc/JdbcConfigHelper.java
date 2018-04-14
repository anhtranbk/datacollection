package com.vcc.bigdata.platform.jdbc;

import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.common.utils.Utils;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class JdbcConfigHelper {

    public static Properties getConnectionPoolProperties() {
        final String path = System.getProperty("jdbc.cp.configuration", "config/tomcat-cp.properties");
        return Utils.loadPropsOrDefault(path);
    }
}
