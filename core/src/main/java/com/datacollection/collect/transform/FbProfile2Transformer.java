package com.datacollection.collect.transform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.datacollection.collect.Constants;
import com.datacollection.collect.OrgSearcher;
import com.datacollection.collect.model.Entity;
import com.datacollection.collect.model.Gender;
import com.datacollection.collect.model.GraphModel;
import com.datacollection.collect.model.History;
import com.datacollection.collect.model.Organization;
import com.datacollection.collect.model.Profile;
import com.datacollection.collect.model.Relationship;
import com.datacollection.common.ProfileRegexHelper;
import com.datacollection.common.utils.NullProtector;
import com.datacollection.entity.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by kumin on 24/11/2017.
 */
public class FbProfile2Transformer implements DataTransformer {

    private ProfileRegexHelper regexHelper;
    private OrgSearcher orgSearcher;


    public FbProfile2Transformer() {
        regexHelper = new ProfileRegexHelper();
        orgSearcher = new OrgSearcher();
    }

    @Override
    public GraphModel transform(Event generic) {
        GraphModel graphModel = new GraphModel();
        try {
            Map<String, Object> post = generic.getProperties();
            History history = new History(generic.getType(), Constants.FACEBOOK, post.get("baseUrl").toString());

            Profile userProfile = new Profile(Profile.TYPE_PERSON);
            userProfile.setHistory(Constants.POST, history);

            if (post.get("ContactInfo") != null) {
                getPhoneAndEmail(userProfile, post);
                getBasicInfo(userProfile, post);
            }
            if (post.get("Living") != null) {
                getAddress(userProfile, post);
            }

            if (post.get("Bio") != null) {
                getBio(userProfile, post);
            }

            Entity fbEntity = new Entity(post.get("userFbId").toString(), "fb.com");
            fbEntity.putProperty("name", post.getOrDefault("name", ""));
            userProfile.addTrustedEntity(new Relationship("account"), fbEntity);

            if (post.get("Education") != null) {
                List<Profile> companyOrOrgProfiles = new ArrayList<>();
                List<Profile> schoolProfiles = new ArrayList<>();
                getJobAndEducation(userProfile, companyOrOrgProfiles, schoolProfiles, post);

                companyOrOrgProfiles.forEach(graphModel::addProfile);
                schoolProfiles.forEach(graphModel::addProfile);
            }
            graphModel.addProfile(userProfile);
        } catch (JsonParseException e) {
            throw new TransformException(e);
        }

        return graphModel;
    }

    private void getPhoneAndEmail(Profile userProfile, Map<String, Object> post) {
        Collection<String> phones = regexHelper.extractPhones(post.get("ContactInfo").toString());
        Collection<String> emails = regexHelper.extractEmails(post.get("ContactInfo").toString());

        phones.forEach(phone -> {
            Entity entity = new Entity(phone, "phone");
            userProfile.addTrustedEntity(entity);
        });

        emails.forEach(email -> {
            Entity entity = new Entity(email, "email");
            userProfile.addTrustedEntity(entity);
        });
    }

    private void getBasicInfo(Profile userProfile, Map<String, Object> post) {
        Map<String, Collection<String>> contactInfo = this.jsonToMap(post.get("ContactInfo").toString());
        Collection<String> basicInfomations = contactInfo.get("Basic Information");
        if (basicInfomations != null)
            basicInfomations.forEach(basicInfomation -> {
                String entityId;
                if (basicInfomation.contains("Birthday") &&
                        (entityId = splitSmart(basicInfomation, ";", 1)) != null) {
                    Entity bday = new Entity(transformBirthday(entityId), "birthday");
                    userProfile.addAnonymousEntity(bday);

                } else if (basicInfomation.contains("Gender") &&
                        (entityId = splitSmart(basicInfomation, ";", 1)) != null) {
                    userProfile.addAnonymousEntity(new Gender(transformGender(entityId)));

                } else if (basicInfomation.contains("Interested In") &&
                        (entityId = splitSmart(basicInfomation, ";", 1)) != null) {
                    Entity interested = new Entity(entityId, "interested_in");
                    userProfile.addAnonymousEntity(interested);
                }
            });
    }

    private void getAddress(Profile userProfile, Map<String, Object> post) {
        Map<String, Collection<String>> living = this.jsonToMap(post.get("Living").toString());
        Collection<String> cityAndHometown = living.get("Current City and Hometown");
        if (cityAndHometown != null)
            cityAndHometown.forEach(addr -> {
                String entityId;
                if (addr.contains("Current city") &&
                        (entityId = splitSmart(addr, ";", 1)) != null) {
                    Entity currentCity = new Entity(entityId, "location");
                    userProfile.addAnonymousEntity(new Relationship("current_city"), currentCity);
                } else if (addr.contains("Hometown") &&
                        (entityId = splitSmart(addr, ";", 1)) != null) {
                    Entity hometown = new Entity(entityId, "location");
                    userProfile.addAnonymousEntity(new Relationship("hometown"), hometown);
                }
            });
    }

    private void getBio(Profile userProfile, Map<String, Object> post) {
        Map<String, Collection<String>> bio = this.jsonToMap(post.get("Bio").toString());
        Collection<String> nicknames = bio.get("Other Names");
        if (nicknames != null)
            nicknames.forEach(nick -> {
                String entityId = splitSmart(nick, ";", 0);
                if (entityId != null) {
                    Entity entity = new Entity(nick.split(";")[0], "nickname");
                    userProfile.addAnonymousEntity(entity);
                }
            });
    }

    private void getJobAndEducation(Profile userProfile, List<Profile> companyOrOrgProfiles,
                                    List<Profile> schoolProfiles, Map<String, Object> post) {
        Map<String, Collection<String>> educationAndWork = this.jsonToMap(post.get("Education").toString());
        Collection<String> works = educationAndWork.get("Work");
        if (works != null)
            works.forEach(work -> {
                String label;
                String orgId = splitSmart(work, ";", 0);
                if (orgId != null) {
                    if (work.toLowerCase().contains("công ty") || work.toLowerCase().contains("company")) {
                        label = "company";
                    } else {
                        label = "org";
                    }
                    List<String> orgNames = orgSearcher.matchOrg(orgId);
                    Organization orgCrawl = new Organization(label, orgId);
                    userProfile.addAnonymousEntity(new Relationship("job", "position",
                            NullProtector.get(work.split(";"), 1).orElse("")), orgCrawl);
                    if (orgNames.isEmpty()) {
                        companyOrOrgProfiles.add(orgCrawl);
                    } else {
                        orgNames.forEach(org -> {
                            Organization orgStandard = new Organization(label, org);
                            orgStandard.addAnonymousEntity(Relationship.forName("looklike"), orgCrawl);
                            companyOrOrgProfiles.add(orgStandard);
                        });
                    }
                }
            });
        Collection<String> schools = educationAndWork.get("Education");
        if (schools != null)
            schools.forEach(school -> {
                String schoolId = splitSmart(school, ";", 0);
                if (schoolId != null) {
                    List<String> schoolNames = orgSearcher.matchOrg(schoolId);
                    Organization schoolCrawl = new Organization("school", schoolId);
                    userProfile.addAnonymousEntity(schoolCrawl);

                    if (schoolNames.isEmpty()) {
                        schoolProfiles.add(schoolCrawl);
                    } else {
                        schoolNames.forEach(name -> {
                            Organization orgStandard = new Organization("school", name);
                            orgStandard.addAnonymousEntity(Relationship.forName("looklike"), schoolCrawl);
                            schoolProfiles.add(orgStandard);
                        });
                    }
                }
            });
    }

    private Map<String, Collection<String>> jsonToMap(String json) {
        Map<String, Collection<String>> outputMap = new HashMap<>();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            String key = jsonObject.get("Title").getAsString();
            JsonArray jsonValues = jsonObject.get("Data").getAsJsonArray();

            Collection<String> values = new HashSet<>();
            for (int j = 0; j < jsonValues.size(); j++) {
                values.add(jsonValues.get(j).getAsString());
            }
            outputMap.put(key, values);
        }
        return outputMap;
    }

    private String transformGender(String gender) {
        if (gender.equals("Male")) return Gender.MALE;
        else if (gender.equals("Female")) return Gender.FEMALE;

        return gender;
    }

    private String transformBirthday(String birthday) {
        DateFormat df1 = new SimpleDateFormat("MMMMMMMMMM dd, yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = df1.parse(birthday);
            return df2.format(date1);
        } catch (ParseException e) {
            return birthday;
        }
    }

    public String splitSmart(String toSplit, String delimiter, int index) {
        String[] split = toSplit.split(delimiter);
        if (split.length > index) return split[index];
        return null;
    }
}