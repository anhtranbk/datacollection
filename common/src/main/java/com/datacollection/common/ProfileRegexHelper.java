package com.datacollection.common;

import com.datacollection.common.io.FileHelper;
import com.datacollection.common.config.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ProfileRegexHelper extends RegexHelper {

    public static final String EMAIL_REGEX_PATH = "config/email-regex.txt";
    public static final String PHONE_REGEX_PATH = "config/phone-regex.txt";

    public static final String DEFAULT_EMAIL_REGEX =
            "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

    public static final String DEFAULT_PHONE_REGEX =
            "([\\s.,\"':;]0\\d{8,10})" + // 0973328004
                    "|([\\s.,\"':;]0\\d{2,3}[ \\.]\\d{3}[ \\.]\\d{4})" + // 097 332 8004
                    "|([\\s.,\"':;]0\\d{3,4}[ \\.]\\d{3}[ \\.]\\d{3})" + // 0973 328 004
                    "|([\\s.,\"':;]0\\d{3,4}[ \\.]\\d{2}[ \\.]\\d{2}[ \\.]\\d{2})" + // 0973 32 80 04
                    "|([\\s.,\"':;]0\\d{2,3}[ \\.]\\d{3}[ \\.]\\d{2}[ \\.]\\d{2})"; // 097 332 80 04

    private static final Logger logger = LoggerFactory.getLogger(ProfileRegexHelper.class);
    private final String emailRegex;
    private final String phoneRegex;

    public ProfileRegexHelper(String emailRegex, String phoneRegex) {
        this.emailRegex = emailRegex;
        this.phoneRegex = phoneRegex;
    }

    public ProfileRegexHelper() {
        try {
            this.emailRegex = FileHelper.readAsString(EMAIL_REGEX_PATH);
            this.phoneRegex = FileHelper.readAsString(PHONE_REGEX_PATH);
            addRegex("email", emailRegex);
            addRegex("phone", phoneRegex);
        } catch (IOException e) {
            logger.warn("Cannot load regex from file, use default values");
            throw new ConfigurationException(e);
        }
    }

    public boolean isEmail(String input) {
        return isMatch("email", input);
    }

    public boolean isPhone(String input) {
        return isMatch("phone", input);
    }

    public Set<String> extractEmails(String input) {
        Set<String> set = new HashSet<>();
        for (String email : listMatched(input, emailRegex)) {
            set.add(email.toLowerCase());
        }
        return set;
    }

    public Set<String> extractPhones(String input) {
        String newInput = preProcessingPhone(input);
        Set<String> set = new HashSet<>();
        for (String phone : listMatched(newInput, phoneRegex)) {
            set.add(phone.replaceAll("\\D", ""));
        }
        return set;
    }

    public String emailRegex() {
        return emailRegex;
    }

    public String phoneRegex() {
        return phoneRegex;
    }

    public static String preProcessingPhone(String content) {
        List<String> regexList = Arrays.asList("\\.", "-", "\\(", "\\)", "\\[", "\\]");
        for (String regex : regexList) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(content);
            content = m.replaceAll(" ");
        }
        Pattern p = Pattern.compile("\\+84");
        Matcher m = p.matcher(content);
        content = m.replaceAll("0");
        return content;
    }

    private static volatile ProfileRegexHelper instance;

    public static synchronized ProfileRegexHelper getDefault() {
        if (instance == null) {
            instance = new ProfileRegexHelper();
            logger.info("Email regex: " + instance.emailRegex());
            logger.info("Phone regex: " + instance.phoneRegex());
        }
        return instance;
    }
}
