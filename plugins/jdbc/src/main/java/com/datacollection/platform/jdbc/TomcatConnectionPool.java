package com.datacollection.platform.jdbc;

import com.datacollection.common.config.Properties;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class TomcatConnectionPool implements ConnectionProvider {

    private DataSource ds;

    @Override
    public void init(JdbcConfig jdbcConfig) throws SQLException {
        ds = new org.apache.tomcat.jdbc.pool.DataSource(
                new MyPoolProperties(jdbcConfig, JdbcConfigHelper.getConnectionPoolProperties()));

        Connection con = ds.getConnection();
        PreparedStatement ps = con.prepareStatement("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
        ps.executeUpdate();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private static class MyPoolProperties extends PoolProperties {

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        public MyPoolProperties(JdbcConfig jdbcConfig, Properties p) {
            this.setTestOnBorrow(p.getBool("pool.testOnBorrow", true));
            this.setTestOnReturn(p.getBool("pool.testOnReturn", true));
            this.setValidationInterval(p.getLong("pool.validationInterval", 30000));
            this.setTimeBetweenEvictionRunsMillis(p.getInt("pool.timeBetweenEvictionRunsMillis", 30000));
            this.setMinEvictableIdleTimeMillis(p.getInt("pool.minEvictableIdleTimeMillis", 30000));
            this.setMaxIdle(p.getInt("pool.maxIdle", 2));
            this.setMaxActive(p.getInt("pool.maxActive", 5));
            this.setInitialSize(p.getInt("pool.initialSize", 2));
            this.setMaxWait(p.getInt("pool.maxWait", 5));
            this.setMinIdle(p.getInt("pool.minIdle", 2));
            this.setRemoveAbandoned(p.getBool("pool.removeAbandoned", true));
            this.setRemoveAbandonedTimeout(p.getInt("pool.removeAbandonedTimeout", 300));
            this.setDriverClassName("com.mysql.jdbc.Driver");
            this.setValidationQuery("SELECT 1");
            this.setLogAbandoned(false);

            this.setUrl(jdbcConfig.getConnectionUrl());
            this.setUsername(jdbcConfig.getUser());
            this.setPassword(jdbcConfig.getPassword());
        }
    }
}
