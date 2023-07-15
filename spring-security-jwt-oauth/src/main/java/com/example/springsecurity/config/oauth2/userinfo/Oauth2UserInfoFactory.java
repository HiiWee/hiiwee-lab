package com.example.springsecurity.config.oauth2.userinfo;

import com.example.springsecurity.config.oauth2.exception.Oauth2ProviderNotFoundException;
import com.example.springsecurity.member.domain.SocialType;
import java.util.Map;

public class Oauth2UserInfoFactory {

    public static Oauth2UserInfo getOauth2UserInfo(final SocialType socialType,
                                                   final Map<String, Object> attributes) {
        if (socialType.equals(SocialType.KAKAO)) {
            return new KakaoOauth2UserInfo(attributes);
        }
        if (socialType.equals(SocialType.GOOGLE)) {
            return null; // 미구현
        }
        if (socialType.equals(SocialType.APPLE)) {
            return null; // 미구현
        }
        throw new Oauth2ProviderNotFoundException("Can't find Oauth2 Provider");
    }
}
