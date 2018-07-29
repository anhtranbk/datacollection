package com.datacollection.graphdb.traversal;

import com.datacollection.graphdb.Direction;
import com.datacollection.graphdb.Edge;
import com.datacollection.graphdb.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Step {

    public final Edge edge;
    public final Vertex vertex;
    public final Direction direction;

    public Step(Edge edge, Vertex vertex, Direction direction) {
        this.edge = edge;
        this.vertex = vertex;
        this.direction = direction;
    }
}
