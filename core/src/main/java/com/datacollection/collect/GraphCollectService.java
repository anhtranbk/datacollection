package com.datacollection.collect;

import com.datacollection.common.config.Configuration;
import com.datacollection.common.utils.JsonUtils;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.datacollection.collect.fbavt.FbAvatarService;
import com.datacollection.collect.filter.CollectFilter;
import com.datacollection.collect.filter.DetectLanguageFilter;
import com.datacollection.collect.filter.DetectSpamFilter;
import com.datacollection.collect.history.HistoryStorage;
import com.datacollection.collect.idgen.RemoteIdGenerator;
import com.datacollection.collect.model.BaseEntity;
import com.datacollection.collect.model.GraphModel;
import com.datacollection.collect.model.History;
import com.datacollection.collect.model.Photo;
import com.datacollection.collect.model.Profile;
import com.datacollection.collect.transform.DataTransformer;
import com.datacollection.collect.transform.TransformException;
import com.datacollection.common.concurrenct.AllInOneFuture;
import com.datacollection.common.concurrenct.FutureAdapter;
import com.datacollection.common.config.Properties;
import com.datacollection.common.types.IdGenerator;
import com.datacollection.common.types.RandomIdGenerator;
import com.datacollection.common.utils.Strings;
import com.datacollection.service.TimeKey;
import com.datacollection.entity.Event;
import com.datacollection.graphdb.Direction;
import com.datacollection.graphdb.Edge;
import com.datacollection.graphdb.GraphDatabase;
import com.datacollection.graphdb.GraphSession;
import com.datacollection.graphdb.Versions;
import com.datacollection.graphdb.Vertex;
import com.datacollection.platform.hystrix.SyncCommand;
import com.datacollection.service.notification.Message;
import com.datacollection.service.notification.NotificationService;
import com.datacollection.service.remoteconfig.RemoteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class GraphCollectService implements CollectService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Collection<CollectFilter> filters = new ArrayList<>();
    private final FbAvatarService fbAvatarService;
    private final NotificationService notificationService;

    private final GraphSession session;
    private final HistoryStorage historyStorage;
    private final Map<String, Integer> dataVersionMapping = new HashMap<>();

    private final IdGenerator idGenerator;
    private final RemoteIdGenerator remoteIdGenerator;

    public GraphCollectService(Configuration conf) {
        this(conf, Arrays.asList(new DetectLanguageFilter(), new DetectSpamFilter()));
    }

    public GraphCollectService(Configuration conf, Collection<CollectFilter> filters) {
        this.filters.addAll(filters);

        this.session = GraphDatabase.open(conf);
        this.historyStorage = HistoryStorage.create(conf);
        this.fbAvatarService = new FbAvatarService(conf);
        this.notificationService = NotificationService.create(conf);

        this.idGenerator = new RandomIdGenerator();
        this.remoteIdGenerator = RemoteIdGenerator.create(conf);

        RemoteConfiguration remoteConfig = RemoteConfiguration.create(conf);
        for (Map.Entry<String, String> e : remoteConfig.getPropertiesByPrefix("_v").entrySet()) {
            String type = e.getKey().split(Versions.DELIMITER)[1];
            dataVersionMapping.put(type, Integer.parseInt(e.getValue()));
        }
    }

    @Override
    public ListenableFuture<?> collect(Event generic) {
        GraphModel gm = transform(generic);
        return gm != null && isValid(gm)
                ? collect(gm)
                : Futures.immediateFuture(0);
    }

    @Override
    public void close() {
        this.session.close();
        this.notificationService.close();
    }

    public ListenableFuture<?> collect(GraphModel gm) {
        List<ListenableFuture<?>> futures = new LinkedList<>();

        for (Profile profile : gm.profiles()) {
            if (Strings.isNullOrEmpty(profile.id())) {
                SyncCommand<String> cmd = new SyncCommand<>("collector", "FindOrCreateUid",
                        () -> findOrCreateUid(profile));
                String uid = cmd.execute();
                if (uid == null) return Futures.immediateFailedFuture(cmd.getException());
                profile.setId(uid);
            }

            saveProfileAndEntities(futures, profile);
            saveRelationships(futures, profile);
            saveUntrustedDataForMatching(futures, profile);

            if (profile.untrustedEntities().isEmpty()) {
                // add message to trigger new uid need to be sync to for-read storage
                Message message = new Message("profile_" + TimeKey.currentTimeKey(), profile.id());
                message.putProperty("uid", profile.id());
                futures.add(notificationService.addMessage(message));
            }

            // save history
            futures.add(historyStorage.addHistory(profile.id(), profile.history()));
        }

        Future<?> fut = AllInOneFuture.from(futures);
        return FutureAdapter.from(fut, list -> list);
    }

    private void saveProfileAndEntities(List<ListenableFuture<?>> futures, Profile profile) {
        History history = profile.history();
        List<Vertex> vertices = new LinkedList<>();

        // add profile entities as vertex
        profile.trustedEntities().forEach(e -> vertices.add(createVertex(e.entity)));
        profile.anonymousEntities().forEach(e -> vertices.add(createVertex(e.entity)));

        profile.untrustedEntities().forEach(e -> {
            Vertex vertex = createVertex(e.entity);
            String log = Strings.join(Constants.PART_DELIMITER, Constants.LOG,
                    history.source(), history.id());
            vertex.putProperty(log, profile.id());
            vertices.add(vertex);
        });

        // add profile vertex
        Vertex vProfile = createVertex(profile);
        Versions.setVersion(history.type(), latestVersionByType(history.type()), vProfile);
        vertices.add(vProfile);

        // save all vertices in one batch
        futures.add(session.addVertices(vertices));
    }

    private void saveRelationships(List<ListenableFuture<?>> futures, Profile profile) {
        History history = profile.history();
        List<Edge> edges = new LinkedList<>();
        Vertex vProfile = Vertex.create(profile.id(), Constants.PROFILE);

        profile.trustedEntities().forEach(e -> {
            Vertex vEntity = createVertex(e.entity);

            Edge edge = Edge.create(e.relationship.name(), vProfile, vEntity, e.relationship.properties());
            Versions.setVersion(history.type(), latestVersionByType(history.type()), edge);
            edges.add(edge);

            Edge reverseEdge = Edge.create(Constants.PROFILE, vEntity, vProfile, e.relationship.properties());
            Versions.setVersion(history.type(), latestVersionByType(history.type()), reverseEdge);
            edges.add(reverseEdge);
        });

        profile.anonymousEntities().forEach(e -> {
            Vertex vEntity = createVertex(e.entity);
            Edge edge = Edge.create(e.relationship.name(), vProfile, vEntity, e.relationship.properties());
            Versions.setVersion(history.type(), latestVersionByType(history.type()), edge);
            edges.add(edge);
        });

        profile.untrustedEntities().forEach(e -> {
            Vertex vEntity = createVertex(e.entity);
            String lb = Constants.HIDDEN_PREFIX + e.relationship.name();

            Edge edge = Edge.create(lb, vProfile, vEntity, e.relationship.properties());
            Versions.setVersion(history.type(), latestVersionByType(history.type()), edge);

            // add history info as extra edge properties to tell where entity appears
            String log = Strings.join(Constants.PART_DELIMITER, Constants.LOG,
                    history.source(), history.id());
            edge.putProperty(log, "");
            edges.add(edge);
        });

        // save all edges in one batch
        futures.add(session.addEdges(edges));
    }

    private void saveUntrustedDataForMatching(List<ListenableFuture<?>> futures, Profile profile) {
        History history = profile.history();

        for (Profile.EntityRelationship e : profile.untrustedEntities()) {
            BaseEntity entity = e.entity;

            // add trace log to trigger has new anonymous data need to be matched
            Message msg = new Message(entity.label(), history.source(), entity.id(), profile.id());
            msg.putProperty("source", history.source());
            msg.putProperty("value", entity.id());
            msg.putProperty("uid", profile.id());
            futures.add(notificationService.addMessage(msg));
        }
    }

    private String findOrCreateUid(Profile profile) {
        List<Photo> photos = new ArrayList<>();
        for (Profile.EntityRelationship e : profile.trustedEntities()) {
            BaseEntity entity = e.entity;
            Vertex vEntity = createVertex(entity);

            // find adjacency vertex link by edge with label profile
            Vertex vProfile = session.vertices(vEntity, Direction.OUT, Constants.PROFILE).first();
            if (vProfile != null) return vProfile.id();

            // if entity is fb account, continue find by fb avatar
            if (!Constants.FACEBOOK.equals(entity.label())) continue;

            String url = fbAvatarService.getAvatarUrl(entity.id());
            if (!url.isEmpty()) {
                Photo photo = new Photo(url, "domain", Constants.FACEBOOK);
                Vertex vPhoto = createVertex(photo);
                vProfile = session.vertices(vPhoto, Direction.OUT, Constants.PROFILE).first();
                if (vProfile != null) return vProfile.id();

                // vProfile != null nghĩa là photo node này chưa được gán với bất kì profile
                // nào (chưa tồn tại trong graph). Lưu lại photo này như là một trusted entity
                photos.add(photo);
            } else {
                // Url empty nghĩa là fb id collect được từ object GenericModel hiện tại là
                // ko hợp lệ (điều này có thể xảy ra do id này thuộc về một app đã bị xóa
                // hoặc trong quá trình crawl việc extract, save dữ liệu có lỗi).
                // Với trường hợp này vẫn lưu lại fbid nhưng coi nó là một dạng đặc biệt (zombie)
                return "fbzb_" + entity.id();
            }
        }

        // save new photos as trusted entities
        photos.forEach(profile::addTrustedEntity);

        return String.valueOf(generateNewId(profile));
    }

    private long generateNewId(Profile profile) {
        long defVal = idGenerator.generate();
        if (profile.trustedEntities().isEmpty()) return defVal;

        List<String> seeds = new ArrayList<>();
        profile.trustedEntities().forEach(e -> seeds.add(e.entity.toString()));

        SyncCommand<Long> cmd = new SyncCommand<>("collector", "RemoteIdGenerator",
                () -> remoteIdGenerator.generate(seeds, defVal), -1L);
        long id = cmd.execute();
        if (id == -1L) throw cmd.getException();
        return id;
    }

    private int latestVersionByType(String type) {
        return dataVersionMapping.getOrDefault(type, Versions.MIN_VERSION);
    }

    private static Vertex createVertex(BaseEntity entity) {
        return Vertex.create(entity.id(), entity.label(), entity.properties());
    }

    private GraphModel transform(Event event) {
        try {
            DataTransformer transformer = DataTransformer.create(event.getType());
            return transformer.transform(event);
        } catch (TransformException e) {
            logger.debug("Record ignored, message [" + e.getMessage() + "] " + event);
        } catch (RuntimeException e) {
            logger.warn("Transform failed: " + JsonUtils.toJson(event), e);
        }
        return null;
    }

    private boolean isValid(GraphModel gm) {
        for (CollectFilter filter : filters) {
            if (!filter.accept(gm)) return false;
        }
        return true;
    }
}
