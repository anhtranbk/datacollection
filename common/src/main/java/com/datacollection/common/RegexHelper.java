package com.datacollection.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class RegexHelper {

    private final Map<String, String> regexes = new HashMap<>();

    public void addRegex(String name, String regex) {
        regexes.put(name, regex);
    }

    public boolean isMatch(String name, String input) {
        String regex = ensureValidName(name);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }

    public Collection<String> extract(String name, String input) {
        String regex = ensureValidName(name);
        return listMatched(input, regex);
    }

    private String ensureValidName(String name) {
        String regex = regexes.get(name);
        if (regex == null) throw new IllegalArgumentException("Not found regex with name: " + name);

        return regexes.get(name);
    }

    public static Collection<String> listMatched(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        Collection<String> results = new LinkedList<>();
        while (matcher.find()) {
            results.add(matcher.group());
        }
        return results;
    }
}
