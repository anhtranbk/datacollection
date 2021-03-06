package com.datacollection.collect.transform;

import com.datacollection.app.collector.Constants;
import com.datacollection.app.collector.model.Entity;
import com.datacollection.app.collector.model.GraphModel;
import com.datacollection.app.collector.model.History;
import com.datacollection.app.collector.model.Profile;
import com.datacollection.app.collector.model.Relationship;
import com.datacollection.common.ParserHelper;
import com.datacollection.common.ProfileRegexHelper;
import com.datacollection.common.utils.Hashings;
import com.datacollection.entity.Event;

import java.util.Map;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ForumCmtTransformer implements DataTransformer {

    private static final ProfileRegexHelper regexHelper = ProfileRegexHelper.getDefault();

    @Override
    public GraphModel transform(Event generic) {
        Map<String, Object> post = generic.getProperties();

        String author = post.getOrDefault("UserPost", "").toString();
        String url = post.get("UrlPage").toString();
        String domain = ParserHelper.parseDomain(url);
        String authorId = ParserHelper.parseForumId(post.getOrDefault("UserPostLink", "").toString());
        String content = post.getOrDefault("Content", "").toString();

        Set<String> emails = regexHelper.extractEmails(content);
        Set<String> phones = regexHelper.extractPhones(content);

        Profile profile = new Profile(Profile.predictTypeByName(author));
        if (authorId != null) {
            profile.addTrustedEntity(Relationship.forName(Constants.ACCOUNT),
                    new Entity(authorId, domain, "name", author));

            String avatar = post.getOrDefault("UserAvatar", "").toString();
            if (!avatar.isEmpty()) {
                profile.addTrustedEntity(new Entity(avatar, Constants.PHOTO, "domain", domain));
            }
        } else {
            if (phones.isEmpty() && emails.isEmpty())
                throw new TransformException("Invalid doc, no user_id or phone/email was extracted");

            // add anonymous authorId from url
            profile.addTrustedEntity(Relationship.forName(Constants.ACCOUNT),
                    new Entity("unknown_" + Hashings.sha1AsHex(url), domain, "name", author));
        }

        phones.forEach(phone -> profile.addUntrustedEntity(new Entity(phone, "phone")));
        emails.forEach(email -> profile.addUntrustedEntity(new Entity(email, "email")));

        History history = new History(generic.getType(), domain, url);
        history.putProperty("content", content);
        history.putProperty("category", post.get("Category"));
        history.putProperty("pdt", post.get("PostDate"));
        history.putProperty("author", authorId != null ? authorId : author);
        history.putProperty("ref", post.get("_id"));

        profile.setHistory(Constants.POST, history);

        return new GraphModel().addProfile(profile);
    }
}
