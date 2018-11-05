package com.datacollection.common.config;

public class SubConfiguration extends Configuration {

    private final Properties parent;
    private final String name;
    private final String group;

    public SubConfiguration(String group, String name, Properties p) {
        this.group = group;
        this.name = name;
        this.parent = p;
    }

    public SubConfiguration(Class<?> clazz, Properties p) {
        this(clazz.getSimpleName(), p);
    }

    public SubConfiguration(String name, Properties p) {
        this(null, name, p);
    }

    @Override
    public String getProperty(String key) {
        for (String prefix : new String[]{name, group}) {
            if (prefix == null) continue;
            String value = parent.getProperty(keyWithPrefix(prefix, key));
            if (value != null) return value;
        }
        return parent.getProperty(key);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return parent.containsKey(keyWithPrefix(name, key))
                || (group != null && parent.containsKey(keyWithPrefix(group, key)))
                || parent.containsKey(key);
    }

    private String keyWithPrefix(String prefix, Object key) {
        return prefix + "." + key;
    }
}
