package com.datacollection.graphdb.traversal;

import com.datacollection.graphdb.Direction;
import com.datacollection.graphdb.GraphSession;
import com.datacollection.graphdb.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class DfsTraversal extends AbstractTraversal {

    public DfsTraversal(GraphSession session, Direction direction, Condition condition, Vertex root) {
        super(session, direction, condition, root);
    }

    @Override
    public Step nextStep() {
        throw new UnsupportedOperationException();
    }
}
