package com.vcc.bigdata.graphdb.cassandra;

import com.google.common.collect.ImmutableMap;
import com.datacollection.common.config.Configuration;
import com.vcc.bigdata.graphdb.Direction;
import com.vcc.bigdata.graphdb.Edge;
import com.vcc.bigdata.graphdb.Vertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CEdgeRepositoryTest {

    private CEdgeRepository repo;

    @Before
    public void setUp() throws Exception {
        Configuration conf = new Configuration();
        conf.setProperty("cassandra.keyspace", "test");
        repo = new CEdgeRepository(conf);

        Vertex vProfile = Vertex.create("8365379162301663585", "profile");
        Vertex vFb = Vertex.create("1659253497647602", "fb.com",
                ImmutableMap.of("name", "Đông Tà", "domain", "fb.com"));
        Vertex vPhone1 = Vertex.create("0903569226", "phone");
        Vertex vEmail = Vertex.create("anhtrannhat@admicro.vn", "email");

        Vertex vAddress = Vertex.create("ha noi", "address");
        Vertex vCompany = Vertex.create("vccorp", "company");
        Vertex vHistory = Vertex.create("https://facebook.com/1659253497647602_1703984579841160", "_log");

        repo.save(Edge.create("account", vProfile, vFb)).get();

        repo.save(Edge.create("_phone", vProfile, vPhone1)).get();
        repo.save(Edge.create("_email", vProfile, vEmail)).get();

        repo.save(Edge.create("work", vProfile, vCompany, ImmutableMap.of("from", 2000))).get();
        repo.save(Edge.create("address", vProfile, vAddress));
        repo.save(Edge.create("_post", vProfile, vHistory));
    }

    @After
    public void tearDown() throws Exception {
//        repo.getSession().execute("TRUNCATE test.vertices");
//        repo.getSession().execute("TRUNCATE test.edges");
    }

    @Test
    public void findByVertex() throws Exception {
        Vertex vProfile = Vertex.create("8365379162301663585", "profile");
        Edge edge = repo.findByVertex(vProfile, Direction.OUT, "_phone").iterator().next();
        assertNotNull(edge);
        assertEquals(vProfile, edge.outVertex());
        assertEquals("0903569226", edge.inVertex().id());

        edge = repo.findByVertex(vProfile, Direction.OUT, "_email").iterator().next();
        assertNotNull(edge);
        assertEquals(vProfile, edge.outVertex());
        assertEquals("anhtrannhat@admicro.vn", edge.inVertex().id());

        edge = repo.findByVertex(vProfile, Direction.OUT, "profile").iterator().next();
        assertNull(edge);

        edge = repo.findByVertex(vProfile, Direction.IN, "_post").iterator().next();
        assertNull(edge);

        edge = repo.findByVertex(vProfile, Direction.OUT, "address").iterator().next();
        assertNotNull(edge);
        assertEquals(vProfile, edge.outVertex());
        assertEquals("ha noi", edge.inVertex().id());

        edge = repo.findByVertex(vProfile, Direction.IN, "work").iterator().next();
        assertNull(edge);
    }

    @Test
    public void findAll() throws Exception {
    }

    @Test
    public void findOne() throws Exception {
    }

    @Test
    public void delete() throws Exception {
    }

    @Test
    public void save() throws Exception {
        Vertex vPerson = Vertex.create("bill", "person");
        Vertex vCompany = Vertex.create("microsoft", "company");
        Edge edge = Edge.create("work", vCompany, vPerson, ImmutableMap.of("from", 2017));
        repo.save(edge);

        Edge edge1 = repo.findByVertex(vPerson, Direction.OUT, "work").iterator().next();
        assertNotNull(edge1);
        assertEquals(edge, edge1);
        assertEquals(1, edge1.properties().size());
        assertEquals("2017", edge1.properties().get("from").toString());

        Edge edge2 = repo.findByVertex(vCompany, Direction.IN, "work").iterator().next();
        assertNotNull(edge2);
        assertEquals(edge, edge2);
        assertEquals(1, edge2.properties().size());
        assertEquals("2017", edge2.properties().get("from").toString());
    }

    @Test
    public void saveAll() throws Exception {
    }

}