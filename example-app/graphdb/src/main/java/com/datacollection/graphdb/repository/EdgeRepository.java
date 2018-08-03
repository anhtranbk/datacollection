package com.datacollection.graphdb.repository;

import com.datacollection.graphdb.Edge;
import com.datacollection.graphdb.Direction;
import com.datacollection.graphdb.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface EdgeRepository extends CrudRepository<Edge> {

    Iterable<Edge> findByVertex(Vertex src, Direction direction, String label);
}
