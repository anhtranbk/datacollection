package com.vcc.bigdata.collect.filter;

import com.vcc.bigdata.collect.model.GraphModel;
import com.vcc.bigdata.common.config.Configurable;
import com.vcc.bigdata.common.config.Properties;

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
