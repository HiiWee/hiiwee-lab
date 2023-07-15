package com.example.springsecurity.config.oauth2.userinfo;

import java.util.Map;

public abstract class Oauth2UserInfo {

    protected final Map<String, Object> attributes;

    public Oauth2UserInfo(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public abstract Long getSocialId();
}
