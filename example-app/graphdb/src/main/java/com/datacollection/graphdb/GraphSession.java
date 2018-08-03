package com.datacollection.graphdb;

import com.datacollection.graphdb.repository.EdgeRepository;
import com.datacollection.graphdb.repository.VertexRepository;

import java.io.Closeable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface GraphSession extends VertexFunctions, EdgeFunctions, Closeable {

    EdgeRepository edgeRepository();

    VertexRepository vertexRepository();

    @Override
    void close();
}
