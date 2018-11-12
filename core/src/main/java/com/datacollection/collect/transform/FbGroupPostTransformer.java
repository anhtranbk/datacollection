package com.datacollection.collect.transform;

import com.google.common.base.Preconditions;
import com.datacollection.app.collector.Constants;
import com.datacollection.app.collector.model.Entity;
import com.datacollection.app.collector.model.GraphModel;
import com.datacollection.app.collector.model.History;
import com.datacollection.app.collector.model.Profile;
import com.datacollection.app.collector.model.Relationship;
import com.datacollection.common.FacebookHelper;
import com.datacollection.common.ParserHelper;
import com.datacollection.common.ProfileRegexHelper;
import com.datacollection.common.collect.Maps;
import com.datacollection.common.utils.Strings;
import com.datacollection.entity.Event;

import java.util.Map;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FbGroupPostTransformer implements DataTransformer {

    private static final ProfileRegexHelper regexHelper = ProfileRegexHelper.getDefault();

    @Override
    public GraphModel transform(Event generic) {
        Map<String, Object> post = generic.getProperties();
        String content = post.get("Content").toString();

        Set<String> emails = regexHelper.extractEmails(content);
        Set<String> phones = regexHelper.extractPhones(content);

        String fromId = Maps.getOrNull(post, "FromId");
        String pageId = post.containsKey("PostFbId")
                ? ParserHelper.parsePageId(post.get("PostFbId").toString()) : null;

        Preconditions.checkNotNull(fromId, "user_id must not be null");

        String name = post.getOrDefault("FromName", "").toString();
        if (name.length() > 500) name = Strings.firstCharacters(name, 500);

        Profile profile = new Profile(Profile.TYPE_PERSON);

        Entity account = new Entity(fromId, Constants.FACEBOOK, "name", name);
        profile.addTrustedEntity(Relationship.forName(Constants.ACCOUNT), account);
        if (pageId != null) {
            profile.addAnonymousEntity(Relationship.forName(Constants.POST),
                    new Entity(pageId, "fbgroup"));
        }

        // add email/phone as untrusted info
        phones.forEach(phone -> profile.addUntrustedEntity(new Entity(phone, "phone")));
        emails.forEach(email -> profile.addUntrustedEntity(new Entity(email, "email")));

        History history = new History(generic.getType(), Constants.FACEBOOK,
                FacebookHelper.FACEBOOK_ENDPOINT + post.get("PostFbId").toString());
        history.putProperty("pdt", post.get("CreateTime"));
        history.putProperty("content", content);
        history.putProperty("mongoid", generic.getId());
        history.putProperty("fbid", fromId);
        history.putProperty("fbpid", pageId);
        history.putProperty("fbpname", Maps.getOrNull(post, "ToName"));

        profile.setHistory(Constants.POST, history);

        return new GraphModel().addProfile(profile);
    }
}
