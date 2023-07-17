package com.example.springsecurity.config.oauth2.userinfo;

import com.example.springsecurity.config.oauth2.exception.Oauth2ProviderNotFoundException;
import com.example.springsecurity.member.domain.SocialType;
import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Oauth2UserInfoFactory {

    public static Oauth2UserInfo getOauth2UserInfo(final SocialType socialType,
                                                   final OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (socialType.equals(SocialType.KAKAO)) {
            return new KakaoOauth2UserInfo(attributes);
        }
        throw new Oauth2ProviderNotFoundException("Can't find Oauth2 Provider");
    }
}
