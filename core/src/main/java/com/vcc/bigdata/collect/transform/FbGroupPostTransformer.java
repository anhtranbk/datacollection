package com.vcc.bigdata.collect.transform;

import com.google.common.base.Preconditions;
import com.vcc.bigdata.collect.Constants;
import com.vcc.bigdata.collect.model.Entity;
import com.vcc.bigdata.collect.model.GraphModel;
import com.vcc.bigdata.collect.model.History;
import com.vcc.bigdata.collect.model.Profile;
import com.vcc.bigdata.collect.model.Relationship;
import com.vcc.bigdata.common.FacebookHelper;
import com.vcc.bigdata.common.ParserHelper;
import com.vcc.bigdata.common.ProfileRegexHelper;
import com.vcc.bigdata.common.utils.Maps;
import com.vcc.bigdata.common.utils.Strings;
import com.vcc.bigdata.extract.model.GenericModel;

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
    public GraphModel transform(GenericModel generic) {
        Map<String, Object> post = generic.getPost();
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
