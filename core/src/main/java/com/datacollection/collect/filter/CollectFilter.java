package com.datacollection.collect.filter;

import com.datacollection.app.collector.model.GraphModel;
import com.datacollection.common.config.Configurable;
import com.datacollection.common.config.Properties;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface CollectFilter extends Configurable {

    @Override
    default void configure(Properties p) {
    }

    boolean accept(GraphModel gm);
}
