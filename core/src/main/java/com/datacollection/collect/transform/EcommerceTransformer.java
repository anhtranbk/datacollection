package com.datacollection.collect.transform;

import com.google.common.base.Preconditions;
import com.datacollection.app.collector.Constants;
import com.datacollection.app.collector.model.Entity;
import com.datacollection.app.collector.model.Gender;
import com.datacollection.app.collector.model.GraphModel;
import com.datacollection.app.collector.model.History;
import com.datacollection.app.collector.model.Profile;
import com.datacollection.app.collector.model.Relationship;
import com.datacollection.common.ProfileRegexHelper;
import com.datacollection.common.utils.Strings;
import com.datacollection.common.utils.Utils;
import com.datacollection.entity.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class EcommerceTransformer implements DataTransformer {

    private ProfileRegexHelper regexHelper;

    public EcommerceTransformer() {
        regexHelper = new ProfileRegexHelper();
    }

    @Override
    public GraphModel transform(Event generic) {
        Map<String, Object> post = generic.getProperties();
        Preconditions.checkNotNull(post);

        String source = convertSource(post.get("source_name").toString());

        History h = new History(generic.getType(), source, post.get("url").toString());
        h.putProperty("type", generic.getType());
        h.putProperties(post);

        String fullName = post.getOrDefault("full_name", "").toString();
        Profile profile = new Profile(Profile.predictTypeByName(fullName));
        profile.setHistory(h);

        if ("vietid".equals(source)) {
            Entity entity = new Entity(post.get("id").toString(), "vietid");
            profile.addTrustedEntity(Relationship.forName(Constants.ACCOUNT), entity);
        } else if ("enbac.com".equals(source) && post.containsKey("user_name")) {
            String username = post.get("user_name").toString();
            Entity entity = new Entity(username, source, "fullname", fullName);
            profile.addTrustedEntity(Relationship.forName(Constants.ACCOUNT), entity);

            post.remove("user_name");
            post.remove("full_name");
        }

        post.remove("url");
        post.remove("id");
        post.remove("source_name");

        for (Map.Entry<String, Object> item : post.entrySet()) {
            String key = item.getKey().toLowerCase();

            if (item.getValue() instanceof ArrayList) {
                for (Object ob : (ArrayList) item.getValue()) {
                    String value = ob.toString();
                    if (Strings.isNonEmpty(value)) {
                        handleKeyValue(source, profile, key, value);
                    }
                }
            } else if (item.getValue() instanceof String) {
                String value = item.getValue().toString();
                if (Strings.isNonEmpty(value)) {
                    handleKeyValue(source, profile, key, value);
                }
            }
        }

        return new GraphModel().addProfile(profile);
    }

    private void handleKeyValue(String source, Profile profile, String key, String value) {
        if ("0".equals(value) && Utils.notEquals(key, "gender")) return;
        switch (key) {
            case "emails":
                Collection<String> emails = regexHelper.extractEmails(value);
                emails.forEach(email -> {
                    Entity entity = new Entity(email, "email");
                    profile.addTrustedEntity(entity);
                });

                break;
            case "phones":
                Collection<String> phones = regexHelper.extractPhones(value);
                phones.forEach(phone -> {
                    Entity entity = new Entity(phone, "phone");
                    profile.addUntrustedEntity(entity);
                });
                break;
            case "avatar_url": {
                if (!value.startsWith("http")) {
                    value = "http://" + source + "/" + value;
                }
                Entity entity = new Entity(value, "photo", "domain", source);
                profile.addTrustedEntity(entity);
                break;
            }
            case "gender": {
                String gender = convertGender(value);
                if (gender != null) {
                    profile.addAnonymousEntity(new Gender(gender));
                }
                break;
            }
            case "birth_day": {
                if (Utils.notEquals("0000-00-00", value)) {
                    Entity entity = new Entity(value, "birthday");
                    profile.addAnonymousEntity(entity);
                }
            }
            case "yahoo_id":
            case "sky_id": {
                Entity entity = new Entity(value, normalizeKey(key));
                profile.addTrustedEntity(Relationship.forName("chat"), entity);
                break;
            }
            case "company": {
                if (Strings.isNonEmpty(value)) {
                    Entity entity = new Entity(value, "company");
                    profile.addAnonymousEntity(Relationship.forName("job"), entity);
                }
            }
            case "user_name":
            case "full_name": {
                Entity entity = new Entity(value, "fullname");
                profile.addAnonymousEntity(entity);
                break;
            }
            default:
                break;
        }
    }

    private static String convertSource(String source) {
        switch (source) {
            case "enbac":
                return "enbac.com";
            case "rongbay":
                return "rongbay.com";
            case "muachung":
                return "muachung.vn";
            case "vietid":
                return "vietid";
            default:
                throw new IllegalArgumentException("Invalid source");
        }
    }

    private static String convertGender(String gender) {
        return "1".equals(gender) ? Gender.FEMALE : null;
    }

    private static String normalizeKey(String key) {
        switch (key) {
            case "sky_id":
                return "skype";
            case "yahoo_id":
                return "yahoo";
            default:
                return key.replace("_", "");
        }
    }
}