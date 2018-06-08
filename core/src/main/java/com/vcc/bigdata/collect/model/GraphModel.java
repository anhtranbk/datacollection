package com.vcc.bigdata.collect.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for profiles and history
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class GraphModel {

    private final List<Profile> profiles = new ArrayList<>();

    public GraphModel addProfile(Profile profile) {
        Preconditions.checkNotNull(profile.historyAction(), "Profile must have history");
        if (Strings.isNullOrEmpty(profile.id())) {
            Preconditions.checkArgument(profile.trustedEntities().size() > 0,
                    "Profile must have at least one trusted entity");
        }

        this.profiles.add(profile);
        return this;
    }

    public List<Profile> profiles() {
        return this.profiles;
    }
}
