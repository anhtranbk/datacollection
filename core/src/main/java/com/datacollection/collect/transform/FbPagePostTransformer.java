package com.datacollection.collect.transform;

import com.google.common.base.Preconditions;
import com.datacollection.app.collector.Constants;
import com.datacollection.app.collector.model.Entity;
import com.datacollection.app.collector.model.FbPage;
import com.datacollection.app.collector.model.GraphModel;
import com.datacollection.app.collector.model.History;
import com.datacollection.app.collector.model.Profile;
import com.datacollection.app.collector.model.Relationship;
import com.datacollection.common.FacebookHelper;
import com.datacollection.common.ParserHelper;
import com.datacollection.common.ProfileRegexHelper;
import com.datacollection.common.utils.Strings;
import com.datacollection.entity.Event;

import java.util.Map;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FbPagePostTransformer implements DataTransformer {

    private static final ProfileRegexHelper regexHelper = ProfileRegexHelper.getDefault();

    @Override
    public GraphModel transform(Event generic) {
        Map<String, Object> post = generic.getProperties();
        String content = post.get("Content").toString();

        String pageId = ParserHelper.parsePageId(post.get("PostFbId").toString());
        String fromId = post.getOrDefault("FromId", pageId).toString();

        Preconditions.checkNotNull(fromId, "user_id must not be null");
        Preconditions.checkNotNull(pageId, "page_id must not be null");

        boolean postByAdmin = fromId.equalsIgnoreCase(pageId);
        Set<String> emails = regexHelper.extractEmails(content);
        Set<String> phones = regexHelper.extractPhones(content);

        // ignore posts that posted by page admin and does not contains any phones or emails
        if (emails.isEmpty() && phones.isEmpty() && postByAdmin) {
            throw new TransformException("Invalid doc, require at least one trusted entity");
        }

        String fromName = post.getOrDefault("FromName", "").toString();
        if (fromName.length() > 150) fromName = Strings.firstCharacters(fromName, 150);

        Profile profile = postByAdmin ? new FbPage(fromId) : new Profile(Profile.TYPE_PERSON);
        if (postByAdmin) {
            profile.putProperty("name", fromName);
        } else {
            Entity account = new Entity(fromId, Constants.FACEBOOK, "name", fromName);
            profile.addTrustedEntity(Relationship.forName("account"), account);

            FbPage fbpage = new FbPage(pageId, "name", post.get("ToName"));
            profile.addAnonymousEntity(Relationship.forName(Constants.POST), fbpage);
        }

        // add email/phone as untrusted info
        phones.forEach(phone -> profile.addUntrustedEntity(new Entity(phone, "phone")));
        emails.forEach(email -> profile.addUntrustedEntity(new Entity(email, "email")));

        // add history
        History history = new History(generic.getType(), Constants.FACEBOOK,
                FacebookHelper.FACEBOOK_ENDPOINT + post.get("PostFbId").toString());
        history.putProperty("pdt", post.get("CreateTime"));
        history.putProperty("content", content);
        history.putProperty("mongoid", generic.getId());
        history.putProperty("fbid", fromId);
        history.putProperty("fbpid", pageId);
        history.putProperty("fbpname", postByAdmin ? post.get("FromName") : post.get("ToName"));

        profile.setHistory(Constants.POST, history);

        return new GraphModel().addProfile(profile);
    }
}
