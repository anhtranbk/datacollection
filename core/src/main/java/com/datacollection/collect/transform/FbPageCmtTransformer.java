package com.datacollection.collect.transform;

import com.google.common.base.Preconditions;
import com.datacollection.collect.Constants;
import com.datacollection.collect.model.Entity;
import com.datacollection.collect.model.FbPage;
import com.datacollection.collect.model.GraphModel;
import com.datacollection.collect.model.History;
import com.datacollection.collect.model.Profile;
import com.datacollection.collect.model.Relationship;
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
public class FbPageCmtTransformer implements DataTransformer {

    private static final ProfileRegexHelper regexHelper = ProfileRegexHelper.getDefault();

    @Override
    public GraphModel transform(Event generic) {
        Map<String, Object> post = generic.getProperties();
        String content = post.get("Content").toString();

        String pageId = ParserHelper.parsePageId(post.get("PostFbId").toString());
        String userId = Maps.getOrNull(post, "UserId");

        Preconditions.checkNotNull(userId, "user_id must not be null");
        Preconditions.checkNotNull(pageId, "page_id must not be null");

        boolean commentByAdmin = userId.equals(pageId);
        Set<String> emails = regexHelper.extractEmails(content);
        Set<String> phones = regexHelper.extractPhones(content);

        // ignore comments that posted by page admin and does not contains any phones or emails
        if (emails.isEmpty() && phones.isEmpty() && commentByAdmin) {
            throw new TransformException("Invalid doc, require at least one trusted entity");
        }

        String username = post.getOrDefault("Username", "").toString();
        if (username.length() > 150) username = Strings.firstCharacters(username, 150);

        Profile profile = commentByAdmin ? new FbPage(pageId) : new Profile(Profile.TYPE_PERSON);
        if (commentByAdmin) {
            profile.putProperty("name", username);
        } else {
            Entity account = new Entity(userId, Constants.FACEBOOK, "name", username);
            profile.addTrustedEntity(Relationship.forName("account"), account);

            FbPage fbpage = new FbPage(pageId);
            profile.addAnonymousEntity(Relationship.forName(Constants.COMMENT), fbpage);
        }

        // add email/phone as untrusted info
        phones.forEach(phone -> profile.addUntrustedEntity(new Entity(phone, "phone")));
        emails.forEach(email -> profile.addUntrustedEntity(new Entity(email, "email")));

        // add history
        History history = new History(generic.getType(), Constants.FACEBOOK,
                FacebookHelper.FACEBOOK_ENDPOINT + post.get("CommentFbId").toString());
        history.putProperty("pdt", post.get("CreateTime"));
        history.putProperty("content", content);
        history.putProperty("mongoid", generic.getId());
        history.putProperty("fbid", userId);
        history.putProperty("postid", post.get("PostFbId"));
        history.putProperty("fbpid", pageId);

        profile.setHistory(Constants.POST, history);

        return new GraphModel().addProfile(profile);
    }
}
