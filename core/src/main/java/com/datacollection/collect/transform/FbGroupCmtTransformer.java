package com.datacollection.collect.transform;

import com.google.common.base.Preconditions;
import com.datacollection.collect.Constants;
import com.datacollection.collect.model.Entity;
import com.datacollection.collect.model.GraphModel;
import com.datacollection.collect.model.History;
import com.datacollection.collect.model.Profile;
import com.datacollection.collect.model.Relationship;
import com.datacollection.common.FacebookHelper;
import com.datacollection.common.ParserHelper;
import com.datacollection.common.ProfileRegexHelper;
import com.datacollection.common.utils.Maps;
import com.datacollection.common.utils.Strings;
import com.datacollection.extract.model.GenericModel;

import java.util.Map;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FbGroupCmtTransformer implements DataTransformer {

    private static final ProfileRegexHelper regexHelper = ProfileRegexHelper.getDefault();

    @Override
    public GraphModel transform(GenericModel generic) {
        Map<String, Object> post = generic.getPost();
        String content = post.get("Content").toString();

        Set<String> emails = regexHelper.extractEmails(content);
        Set<String> phones = regexHelper.extractPhones(content);

        String pageId = post.containsKey("PostFbId")
                ? ParserHelper.parsePageId(post.get("PostFbId").toString()) : null;
        String userId = Maps.getOrNull(post, "UserId");

        Preconditions.checkNotNull(userId, "user_id must not be null");

        String name = post.getOrDefault("Username", "").toString();
        if (name.length() > 500) name = Strings.firstCharacters(name, 500);

        Profile profile = new Profile(Profile.TYPE_PERSON);

        Entity account = new Entity(userId, Constants.FACEBOOK, "name", name);
        profile.addTrustedEntity(Relationship.forName(Constants.ACCOUNT), account);
        if (pageId != null) {
            profile.addAnonymousEntity(Relationship.forName(Constants.COMMENT),
                    new Entity(pageId, "fbgroup"));
        }

        // add email/phone as untrusted info
        phones.forEach(phone -> profile.addUntrustedEntity(new Entity(phone, "phone")));
        emails.forEach(email -> profile.addUntrustedEntity(new Entity(email, "email")));

        History history = new History(generic.getType(), Constants.FACEBOOK,
                FacebookHelper.FACEBOOK_ENDPOINT + post.get("CommentFbId").toString());
        history.putProperty("pdt", post.get("CreateTime"));
        history.putProperty("content", content);
        history.putProperty("mongoid", generic.getId());
        history.putProperty("fbid", userId);
        history.putProperty("fbpid", pageId);
        history.putProperty("postid", post.get("PostFbId"));

        profile.setHistory(Constants.POST, history);

        return new GraphModel().addProfile(profile);
    }
}
