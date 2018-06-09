package com.vcc.bigdata.collect.transform;

import com.vcc.bigdata.collect.Constants;
import com.vcc.bigdata.collect.model.Entity;
import com.vcc.bigdata.collect.model.GraphModel;
import com.vcc.bigdata.collect.model.History;
import com.vcc.bigdata.collect.model.Profile;
import com.vcc.bigdata.collect.model.Relationship;
import com.vcc.bigdata.common.ParserHelper;
import com.vcc.bigdata.common.ProfileRegexHelper;
import com.vcc.bigdata.common.utils.Hashings;
import com.vcc.bigdata.extract.model.GenericModel;

import java.util.Map;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ForumArtTransformer implements DataTransformer {

    private static final ProfileRegexHelper regexHelper = ProfileRegexHelper.getDefault();

    @Override
    public GraphModel transform(GenericModel generic) {
        Map<String, Object> post = generic.getPost();

        String author = post.getOrDefault("Author", "").toString();
        String url = post.get("Url").toString();
        String domain = post.get("Domain").toString();
        String authorId = ParserHelper.parseForumId(post.getOrDefault("AuthorLink", "").toString());
        String content = post.getOrDefault("Content", "").toString();

        Set<String> emails = regexHelper.extractEmails(content);
        Set<String> phones = regexHelper.extractPhones(content);

        Profile profile = new Profile(Profile.predictTypeByName(author));
        if (authorId != null) {
            profile.addTrustedEntity(Relationship.forName(Constants.ACCOUNT),
                    new Entity(authorId, domain, "name", author));

            String avatar = post.getOrDefault("PathAvatar", "").toString();
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
        history.putProperty("title", post.get("Title"));
        history.putProperty("category", post.get("Category"));
        history.putProperty("pdt", post.get("PostDate"));
        history.putProperty("author", authorId != null ? authorId : author);
        history.putProperty("ref", post.get("_id"));

        profile.setHistory(Constants.POST, history);

        return new GraphModel().addProfile(profile);
    }
}