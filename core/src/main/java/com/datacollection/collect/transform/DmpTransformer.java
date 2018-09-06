package com.datacollection.collect.transform;

import com.datacollection.collect.model.Entity;
import com.datacollection.collect.model.GraphModel;
import com.datacollection.collect.model.History;
import com.datacollection.collect.model.Profile;
import com.datacollection.collect.model.Relationship;
import com.datacollection.common.ProfileRegexHelper;
import com.datacollection.entity.GenericModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DmpTransformer implements DataTransformer {

    private ProfileRegexHelper regexHelper;

    public DmpTransformer() {
        regexHelper = new ProfileRegexHelper();
    }

    @SuppressWarnings("unchecked")
    @Override
    public GraphModel transform(GenericModel generic) {
        if (generic.getId().equals("-1")) return null;
        Map<String, Object> post = generic.getProperties();

        GraphModel graphModel = new GraphModel();

        Profile userProfile = new Profile(Profile.TYPE_PERSON);
        History history = new History(generic.getType(), post.get("domain").toString(),
                "http://10.3.34.84:9200/dmp_*/" + generic.getId());

        if (post.get("viet_id") != null) {
            Collection<Integer> vietids = (Collection<Integer>) post.get("viet_id");
            vietids.forEach(vietid -> {
                Entity entity = new Entity(vietid.toString(), "vietid");
                userProfile.addTrustedEntity(Relationship.forName("account"), entity);
            });

        }

        if (post.get("phones") != null) {
            Collection<String> phones = (Collection<String>) post.get("phones");
            phones.forEach(phone -> {
                Collection<String> phonesRegp = this.extractPhones(phone);
                if (!phonesRegp.isEmpty()) {
                    phone = phonesRegp.iterator().next();
                    Entity entity = new Entity(phone, "phone");
                    userProfile.addUntrustedEntity(entity);
                }
            });
        }

        if (post.get("emails") != null) {
            Collection<String> emails = (Collection<String>) post.get("emails");
            emails.forEach(email -> {
                Collection<String> emailsRegp = this.extractEmails(email);
                if (!emailsRegp.isEmpty()) {
                    email = emailsRegp.iterator().next();
                    Entity entity = new Entity(email, "email");
                    userProfile.addUntrustedEntity(entity);
                }
            });
        }

        Map<String, Object> props = getDemographics(post, Arrays.asList("gender", "age"));
        Entity entity = new Entity(post.get("guid").toString(), "adsid", props);
        userProfile.addTrustedEntity(entity);

        history.putProperty("post", post);
        userProfile.setHistory(history);
        graphModel.addProfile(userProfile);

        return graphModel;
    }

    public Map<String, Object> getDemographics(Map<String, Object> post, List<String> infoNames) {
        Map<String, Object> props = new HashMap<>();
        infoNames.forEach(name -> {
            if (post.get(name) != null) {
                props.put(name, post.get(name).toString());
            }
        });

        return props;
    }

    private Set<String> extractEmails(String content) {
        return regexHelper.extractEmails(content);
    }

    private Set<String> extractPhones(String content) {
        Set<String> phonesExtract = regexHelper.extractPhones(content);
        Set<String> phones = new HashSet<>();
        for (String phone : phonesExtract) {
            phone = phone.trim().replaceAll("[^0-9]", "");
            phone = phone.startsWith("0") ? phone : "0" + phone;
            phones.addAll(regexHelper.extractPhones(phone));
        }
        return phones;
    }
}
