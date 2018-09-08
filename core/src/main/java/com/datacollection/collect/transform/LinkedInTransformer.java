package com.datacollection.collect.transform;

import com.datacollection.collect.OrgSearcher;
import com.datacollection.collect.Constants;
import com.datacollection.collect.model.Entity;
import com.datacollection.collect.model.GraphModel;
import com.datacollection.collect.model.History;
import com.datacollection.collect.model.Organization;
import com.datacollection.collect.model.Profile;
import com.datacollection.collect.model.Relationship;
import com.datacollection.common.utils.Strings;
import com.datacollection.entity.Event;

import java.util.List;
import java.util.Map;

public class LinkedInTransformer implements DataTransformer {
    private OrgSearcher orgSearcher;

    public LinkedInTransformer() {
        orgSearcher = new OrgSearcher();
    }

    @Override
    public GraphModel transform(Event generic) {
        GraphModel graphModel = new GraphModel();
        Map<String, Object> post = generic.getProperties();

        Profile person = new Profile(Profile.TYPE_PERSON);
        History history = new History(generic.getType(), "linkedin", post.get("Url").toString());
        person.setHistory(Constants.POST, history);

        String[] splitStr = post.get("Url").toString().split("/");

        person.addTrustedEntity(new Entity(splitStr[4], "account"));

        if (!Strings.isNullOrStringEmpty(post.get("Name"))) {
            person.addAnonymousEntity(new Entity(post.get("Name").toString(), "name"));
        }

        if (!Strings.isNullOrStringEmpty(post.get("Experience"))) {
            List<Map<String, Object>> mExperience = (List<Map<String, Object>>) post.get("Experience");
            for (Map<String, Object> mE : mExperience) {
                Relationship relationship = new Relationship("job");
                relationship.putProperty("position", mE.get("Position"));
                relationship.putProperty("duration", mE.get("Duration"));

                String[] time = mE.get("Times").toString().split("–");
                relationship.putProperty("from", time[0].trim());
                relationship.putProperty("to", time[1].trim());

                Organization companyCrawl = new Organization(mE.get("Company").toString(), Profile.TYPE_COMPANY);
                person.addAnonymousEntity(relationship, companyCrawl);
                List<String> companyNames = orgSearcher.matchOrg(mE.get("Company").toString());

                if (companyNames.isEmpty()) {
                    graphModel.addProfile(companyCrawl);
                } else {
                    companyNames.forEach(name -> {
                        Organization companyStandard = new Organization(name, Profile.TYPE_COMPANY);
                        companyStandard.addAnonymousEntity(Relationship.forName("looklike"), companyCrawl);
                        graphModel.addProfile(companyStandard);
                    });
                }
            }
        }

        if (!Strings.isNullOrStringEmpty(post.get("Education"))) {
            List<Map<String, Object>> mEducation = (List<Map<String, Object>>) post.get("Education");
            for (Map<String, Object> mEdu : mEducation) {
                Relationship relationship = new Relationship("school");
                relationship.putProperty("times", mEdu.get("Times"));
                relationship.putProperty("degreeName", mEdu.get("DegreeName"));
                relationship.putProperty("fieldofstudy", mEdu.get("FieldOfStudy"));


                Organization schoolCrawl = new Organization(mEdu.get("SchoolName").toString(), Profile.TYPE_SCHOOL);
                person.addAnonymousEntity(relationship, schoolCrawl);
                List<String> schoolNames = orgSearcher.matchOrg(mEdu.get("SchoolName").toString());

                if(schoolNames.isEmpty()){
                    graphModel.addProfile(schoolCrawl);
                }else {
                    schoolNames.forEach(name ->{
                        Organization schoolStandard = new Organization(name, Profile.TYPE_SCHOOL);
                        schoolStandard.addAnonymousEntity(Relationship.forName("looklike"), schoolCrawl);
                        graphModel.addProfile(schoolStandard);
                    });
                }
            }
        }

        if (!Strings.isNullOrStringEmpty(post.get("FeaturedSkills_Endorsements"))) {
            List<Map<String, Object>> linkedInSkill = (List<Map<String, Object>>) post.get("FeaturedSkills_Endorsements");
            for (Map<String, Object> linkedIn : linkedInSkill) {
                if (!Strings.isNullOrStringEmpty(linkedIn.get("Name"))) {
                    person.addAnonymousEntity(new Entity(linkedIn.get("Name").toString(), "linkedinskill"));
                }
            }
        }

        if (!Strings.isNullOrStringEmpty(post.get("Interests"))) {
            List<Map<String, Object>> linkedInInterests = (List<Map<String, Object>>) post.get("Interests");
            for (Map<String, Object> linkedIn : linkedInInterests) {
                if (!Strings.isNullOrStringEmpty(linkedIn.get("Name"))) {
                    person.addAnonymousEntity(new Entity(linkedIn.get("Name").toString(), "interests"));
                }
            }
        }

        graphModel.addProfile(person);
        return graphModel;
    }

//    public static void main(String[] args) throws IOException {
//        String JSON_SOURCE = "{\"_id\":\"eff719abd882a7b0b4b1c5b83aec4ed9\",\"Url\":\"https://www.linkedin.com/in/lam-pham-thanh-1428602b/\",\"Name\":\"Lam Pham Thanh\",\"Work\":\"Data Scientist at Datalab, VNG.\",\"Comapany\":\"\",\"Description\":\"I am: 5 years of hands-on experience in Data Analytics, 10 years of Business Intelligence and Solution Implementation, 15 years of writing code. - Data Scientist at Datalab, VNG Corp. - Senior Data Scientist at Sentif AG Group, building text understanding intelligence, Deep/Machine Learning practitioner. I got single silver medal of Quora question pair duplication on Kaggle, semi finalist of Analytic Cup CIKM 2017(rank 8th), top 10 WSDM Churn Prediction Analytic Cup, completed 3 courses of Deep learning on Coursera in 4 days. - Founder of SaigonApps startup: building apps on top of state of the art technologies in Computer Vision and Machine Learning field. - Speaker at PyconSG 2016, Vietnamwork TechExpo Hanoi and HCMC 2016, VACC 2016 - Data Scientist at Viettel CyberSpace Center - Solution Architect in Intelligent Transportation Systems (ITS) and Social Mobile Analytic Cloud (SMAC) solutions and applications of FPT Technology Solutions. - Product Manager of cross platform mobile applications, GNT Inc. - Speaker at tech-startup conferences like BarcampSaigon, FPT techdays, mobile hackathon. - Hacker and data lover of the Big Data. - Full time Father of Yummy and Tony. My github: https://github.com/lampts/data_science/blob/master/README.md I have: - 15 years of hands-on experience in ICT industry, big scale projects. - 5 years in data mining, machine learning and applied deep learning. - 5 years of full cycle developing mobile applications on cross platforms. - 3 years in Intelligent Transportation System hands-on products. Master degree, graduated from Saint Petersburg State (Russia) and Heilbronn (Germany) Universities Excellent diploma from Russian University\",\"Experience\":[{\"Position\":\"Kaggle Data Hacker\",\"Company\":\"Saigonapps\",\"Times\":\"May 2017 – Present\",\"Duration\":\"9 mos\",\"Location\":\"Internet\",\"Description\":\"- Top 3%, Silver Medal, Quora question pairs competition (rank 78th/3300+) https://www.kaggle.com/c/quora-question-pairs Media (1) This position has 1 media\"},{\"Position\":\"Kaggle Data Hacker\",\"Company\":\"Saigonapps\",\"Times\":\"May 2017 – Present\",\"Duration\":\"9 mos\",\"Location\":\"Internet\",\"Description\":\"- Top 3%, Silver Medal, Quora question pairs competition (rank 78th/3300+) https://www.kaggle.com/c/quora-question-pairs\"},{\"Position\":\"Senior Data Scientist\",\"Company\":\"Sentifi\",\"Times\":\"May 2016 – Present\",\"Duration\":\"1 yr 9 mos\",\"Location\":\"HCMC\",\"Description\":\"As a senior data scientist, I have to design/build/solve big data scale scoring systems in order to extract information, knowledge representation from social media platforms for finance. Moreover, I'm actively involved in consulting on end-to-end machine learning/ real world deep learning system for NLP/CV.\"},{\"Position\":\"Founder\",\"Company\":\"appsaigon.com\",\"Times\":\"Jan 2015 – Present\",\"Duration\":\"3 yrs 1 mo\",\"Location\":\"Saigon\",\"Description\":\"I am responsible for building topnotch apps on mobile platforms such as iOS, Android. Basically I present my ideas with co-founders after doing market research, sketch the prototypes, design APIs and logic flows at backend side then do integration and sanity check before submitting on Appstore. Every single day, I am happy to monitor and reply to users feedbacks and questions. What I achieved is our 3 apps got in top #100 in travel and music category TrumMP3 and VNCam reached top 50th of Vietnam store in first month of release. Users love this everyday need app before beating the traffic during peak period in Saigon or Hanoi city. Our core product is AI and precision recommendation is coming soon.\"},{\"Position\":\"Solution Architect\",\"Company\":\"FTS\",\"Times\":\"Apr 2012 – Present\",\"Duration\":\"5 yrs 10 mos\",\"Location\":\"HCMC\",\"Description\":\"Apply solutions for social projects. VMS project: * http://vnexpress.net/gl/xa-hoi/2012/11/tp-hcm-canh-bao-ket-xe-bang-bien-bao-dien-tu/ * http://chungta.vn/tin-tuc/kinh-doanh/2012/11/thi-diem-bang-quang-bao-dien-tu-den-thang-4-2013/ Traffic Pattern Analysis: * unique solution for analysing traffic volume, density, flow (esp. for motorcycle,...) Traffic on the go: Notis - crowd sourcing traffic application * see at http://notis.vn * http://www.tienphong.vn/Gioi-Tre/chang-trai-chong-un-tac-giao-thong-bang-cong-nghe-709504.tpo * http://chungta.vn/tin-tuc/cong-nghe/2014/06/dung-notis-khong-lo-ket-xe/\"}],\"Education\":[{\"SchoolName\":\"VGU university\",\"DegreeName\":\"Master\",\"FieldOfStudy\":\"Business information system\",\"Grade\":\"\",\"Times\":\"Dates attended or expected graduation 2010 – 2012\"},{\"SchoolName\":\"Saint Petersburg state university\",\"DegreeName\":\"Master\",\"FieldOfStudy\":\"Math - Computer Science\",\"Grade\":\"\",\"Times\":\"Dates attended or expected graduation 2000 – 2007\"},{\"SchoolName\":\"Dai hoc Quôc Gia Tp. Hô Chí Minh\",\"DegreeName\":\"\",\"FieldOfStudy\":\"\",\"Grade\":\"\",\"Times\":\"\"}],\"FeaturedSkills_Endorsements\":[{\"Name\":\"Software Project Management\",\"Decription\":\"Endorsed by Henry (Hung) Pham, MSc, PMP, who is highly skilled at this\"},{\"Name\":\"\",\"Decription\":\"Endorsed by 4 of Lam’s colleagues at FPT Software\"},{\"Name\":\"Software Development\",\"Decription\":\"Endorsed by 3 of Lam’s colleagues at FPT Software\"},{\"Name\":\"ERP\",\"Decription\":\"Hung Huynh and 10 connections have given endorsements for this skill\"}],\"Interests\":[{\"Name\":\"DataRobot\",\"Decription\":\"\",\"Follow\":\"9,646 followers\"},{\"Name\":\"Career.vn\",\"Decription\":\"\",\"Follow\":\"20,093 members\"},{\"Name\":\"Vietnam Jobs\",\"Decription\":\"\",\"Follow\":\"26,894 members\"},{\"Name\":\"Information Technology Managers\",\"Decription\":\"\",\"Follow\":\"8,069 members\"},{\"Name\":\"Raytheon\",\"Decription\":\"\",\"Follow\":\"282,808 followers\"},{\"Name\":\"ERP4VN Group\",\"Decription\":\"\",\"Follow\":\"2,504 members\"}],\"Langguage\":\"\",\"PostDate\":1516092950382}";
//        LinkedInTransformer linkedInTransformer = new LinkedInTransformer();
////        linkedInTransformer.transform(new GenericModel("1212",null,Document.parse(JSON_SOURCE), UserInfo.empty()));
//        System.out.println(linkedInTransformer.transform(new GenericModel("1212",null,Document.parse(JSON_SOURCE), UserInfo.empty())));
//    }

}
