package com.example.springsecurity.config.oauth2.userinfo;

import java.util.Map;

public class KakaoOauth2UserInfo extends Oauth2UserInfo {

    private final Map<String, Object> properties;
    private final Map<String, Object> kakaoAccounts;

    public KakaoOauth2UserInfo(final Map<String, Object> attributes) {
        super(attributes);
        this.properties = (Map<String, Object>) attributes.get("properties");
        this.kakaoAccounts = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getName() {
        return (String) properties.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccounts.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) properties.get("profile_image");
    }

    @Override
    public Long getSocialId() {
        return (Long) attributes.get("id");
    }
}
