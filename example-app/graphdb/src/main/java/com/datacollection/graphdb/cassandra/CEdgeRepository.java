package com.datacollection.graphdb.cassandra;

import com.datacollection.graphdb.Direction;
import com.datacollection.graphdb.Edge;
import com.datacollection.graphdb.Vertex;
import com.datacollection.graphdb.repository.EdgeRepository;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.datacollection.common.concurrenct.FutureAdapter;
import com.datacollection.common.config.Properties;
import com.datacollection.common.collect.IterableAdapter;
import com.datacollection.common.collect.Maps;
import com.datacollection.common.utils.Utils;
import com.datacollection.platform.cassandra.AbstractRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CEdgeRepository extends AbstractRepository implements EdgeRepository {

    public static final String DIRECTION_INCOMING = "i";
    public static final String DIRECTION_OUTGOING = "o";

    private static final AtomicBoolean statementPrepared = new AtomicBoolean(false);
    private static PreparedStatement psUpdate, psDelete;

    public CEdgeRepository(Properties props) {
        super(props);
        if (statementPrepared.compareAndSet(false, true)) {
            psUpdate = session.prepare("UPDATE edges SET dstlb = ?, p = p + ? " +
                    "WHERE srclb = ? AND srcid = ? AND d = ? AND label = ? AND dstid = ?");
            psDelete = session.prepare("DELETE FROM edges WHERE srclb = ? AND srcid = ? " +
                    "AND d = ? AND label = ? AND dstid = ?");
        }
    }

    @Override
    public Iterable<Edge> findByVertex(Vertex src, Direction direction, String label) {
        Preconditions.checkArgument(label == null || Utils.notEquals(direction, Direction.BOTH),
                "Can only query bi-direction edge if label null");

        String query = "SELECT * FROM edges WHERE srclb = ? AND srcid = ?";
        ResultSet rs;
        if (label == null) {
            if (direction.equals(Direction.BOTH)) {
                rs = session.execute(query, src.label(), src.id());
            } else {
                query += " AND d = ?";
                rs = session.execute(query, src.label(), src.id(), directionToString(direction));
            }
        } else {
            query += " AND d = ? AND label = ?";
            rs = session.execute(query, src.label(), src.id(), directionToString(direction), label);
        }

        return IterableAdapter.from(rs, row -> {
            String elb = row.getString("label");
            Direction d = directionFromString(row.getString("d"));
            Vertex dst = Vertex.create(row.getString("dstid"), row.getString("dstlb"));
            Map<String, String> props = row.getMap("p", String.class, String.class);

            return Direction.OUT.equals(d)
                    ? Edge.create(elb, src, dst, props)
                    : Edge.create(elb, dst, src, props);
        });
    }

    @Override
    public Iterable<Edge> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Edge findOne(Edge entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListenableFuture<Iterable<Edge>> delete(Edge entity) {
        BatchStatement bs = new BatchStatement();
        bs.add(psDelete.bind(
                entity.outVertex().label(),
                entity.outVertex().id(),
                DIRECTION_OUTGOING,
                entity.label(),
                entity.inVertex().id()));

        bs.add(psDelete.bind(
                entity.inVertex().label(),
                entity.inVertex().id(),
                DIRECTION_INCOMING,
                entity.label(),
                entity.outVertex().id()));

        return FutureAdapter.from(session.executeAsync(bs), rs -> Collections.singleton(entity));
    }

    @Override
    public ListenableFuture<Iterable<Edge>> save(Edge entity) {
        return saveAll(Collections.singleton(entity));
    }

    @Override
    public ListenableFuture<Iterable<Edge>> saveAll(Collection<Edge> entities) {
        BatchStatement bs = new BatchStatement();
        for (Edge entity : entities) {
            bs.add(psUpdate.bind(
                    entity.outVertex().label(),
                    Maps.convertToTextMap(entity.properties()),
                    entity.inVertex().label(),
                    entity.inVertex().id(),
                    DIRECTION_INCOMING,
                    entity.label(),
                    entity.outVertex().id()));

            bs.add(psUpdate.bind(
                    entity.inVertex().label(),
                    Maps.convertToTextMap(entity.properties()),
                    entity.outVertex().label(),
                    entity.outVertex().id(),
                    DIRECTION_OUTGOING,
                    entity.label(),
                    entity.inVertex().id()));
        }
        return FutureAdapter.from(session.executeAsync(bs), rs -> entities);
    }

    private static String directionToString(Direction direction) {
        return direction.equals(Direction.OUT) ? DIRECTION_OUTGOING : DIRECTION_INCOMING;
    }

    private static Direction directionFromString(String directionString) {
        return directionString.equalsIgnoreCase(DIRECTION_OUTGOING) ? Direction.OUT : Direction.IN;
    }
}
