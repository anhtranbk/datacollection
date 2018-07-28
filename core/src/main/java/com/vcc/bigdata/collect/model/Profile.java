package com.vcc.bigdata.collect.model;

import com.vcc.bigdata.collect.Constants;
import com.datacollection.common.utils.Maps;
import com.datacollection.common.utils.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Profile extends Entity {

    public static final String TYPE_PERSON = "person";
    public static final String TYPE_COMPANY = "company";
    public static final String TYPE_SCHOOL = "school";
    public static final String TYPE_ORG = "org";
    public static final String TYPE_FBPAGE = "fbpage";

    private final String type;
    private final List<EntityRelationship> untrusted = new ArrayList<>();
    private final List<EntityRelationship> trusted = new ArrayList<>();
    private final List<EntityRelationship> anonymous = new ArrayList<>();
    private HistoryAction historyAction;

    public Profile(String type) {
        this(type, null, Collections.emptyMap());
    }

    public Profile(String type, String id) {
        this(type, id, Collections.emptyMap());
    }

    public Profile(String type, String id, Object... keyValues) {
        this(type, id, Maps.initFromKeyValues(keyValues));
    }

    public Profile(String type, String id, Map<String, Object> properties) {
        super(id, Constants.PROFILE, properties);
        this.type = type;
        this.putProperty("tag", type);
        this.putProperty("_ts", System.currentTimeMillis());
    }

    public String type() {
        return type;
    }

    public Profile addUntrustedEntity(Relationship relationship, BaseEntity entity) {
        untrusted.add(new EntityRelationship(relationship, entity));
        return this;
    }

    public Profile addUntrustedEntity(BaseEntity entity) {
        return addUntrustedEntity(new Relationship(entity.label()), entity);
    }

    public Profile addTrustedEntity(Relationship relationship, BaseEntity entity) {
        trusted.add(new EntityRelationship(relationship, entity));
        return this;
    }

    public Profile addTrustedEntity(BaseEntity entity) {
        return addTrustedEntity(new Relationship(entity.label()), entity);
    }

    public Profile addAnonymousEntity(Relationship relationship, BaseEntity entity) {
        anonymous.add(new EntityRelationship(relationship, entity));
        return this;
    }

    public Profile addAnonymousEntity(BaseEntity entity) {
        return addAnonymousEntity(new Relationship(entity.label()), entity);
    }

    public List<EntityRelationship> untrustedEntities() {
        return untrusted;
    }

    public List<EntityRelationship> trustedEntities() {
        return trusted;
    }

    public List<EntityRelationship> anonymousEntities() {
        return anonymous;
    }

    public Profile setHistory(History history) {
        return setHistory(Constants.LOG, history);
    }

    public Profile setHistory(String action, History history) {
        this.historyAction = new HistoryAction(action, history);
        return this;
    }

    public HistoryAction historyAction() {
        return this.historyAction;
    }

    public History history() {
        return historyAction != null ? historyAction.history : null;
    }

    public static class HistoryAction {
        public final String action;
        public final History history;

        public HistoryAction(String action, History history) {
            this.action = action;
            this.history = history;
        }
    }

    public static class EntityRelationship {
        public final BaseEntity entity;
        public final Relationship relationship;

        public EntityRelationship(Relationship relationship, BaseEntity entity) {
            this.entity = entity;
            this.relationship = relationship;
        }
    }

    public static String predictTypeByName(String name) {
        if (Strings.containsOnce(name, "tập đoàn", "tap doan", "công ty", "cty", "c.ty", "cong ty"))
            return TYPE_COMPANY;
        return TYPE_PERSON;
    }
}
