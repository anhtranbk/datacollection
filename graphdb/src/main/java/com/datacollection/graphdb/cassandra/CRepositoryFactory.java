package com.datacollection.graphdb.cassandra;

import com.datacollection.graphdb.repository.VertexRepository;
import com.google.common.base.Preconditions;
import com.datacollection.common.config.Properties;
import com.datacollection.graphdb.repository.EdgeRepository;
import com.datacollection.graphdb.repository.RepositoryFactory;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CRepositoryFactory implements RepositoryFactory {

    private Properties props;

    @Override
    public EdgeRepository edgeRepository() {
        Preconditions.checkNotNull(props, "Repository Factory must be configure first");
        return new CEdgeRepository(props);
    }

    @Override
    public VertexRepository vertexRepository() {
        Preconditions.checkNotNull(props, "Repository Factory must be configure first");
        return new CVertexRepository(props);
    }

    @Override
    public void configure(Properties p) {
        this.props = p;
    }
}
