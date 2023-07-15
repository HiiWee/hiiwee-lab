package com.example.springsecurity.config.oauth2.service;

import com.example.springsecurity.config.oauth2.userinfo.Oauth2UserInfo;
import com.example.springsecurity.config.oauth2.userinfo.Oauth2UserInfoFactory;
import com.example.springsecurity.member.domain.Member;
import com.example.springsecurity.member.domain.Role;
import com.example.springsecurity.member.domain.SocialType;
import com.example.springsecurity.member.repository.MemberRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    public CustomOauth2UserService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        SocialType socialType = SocialType.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        Oauth2UserInfo oauth2UserInfo = Oauth2UserInfoFactory.getOauth2UserInfo(socialType,
                oAuth2User.getAttributes());
        validateOauthUserInfo(oauth2UserInfo);
        if (!memberRepository.existsByEmailAndSocialId(oauth2UserInfo.getEmail(), oauth2UserInfo.getSocialId())) {
            registerNewMember(oauth2UserInfo, socialType);
        }
        updateExistingMember(oauth2UserInfo);

        return createOauth2User(oauth2UserInfo);
    }

    private Oauth2UserPrincipal createOauth2User(final Oauth2UserInfo oauth2UserInfo) {
        Member member = memberRepository.findByEmail(oauth2UserInfo.getEmail()).get();
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(member.getRole().toString());
        return Oauth2UserPrincipal.builder()
                .memberId(member.getId())
                .authorities(authorities)
                .build();
    }

    private void updateExistingMember(final Oauth2UserInfo oauth2UserInfo) {
        Member member = memberRepository.findByEmail(oauth2UserInfo.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        member.updateName(oauth2UserInfo.getName());
        member.updateImageUrl(oauth2UserInfo.getImageUrl());
    }

    private void registerNewMember(final Oauth2UserInfo oauth2UserInfo, final SocialType socialType) {
        Member member = Member.builder()
                .name(oauth2UserInfo.getName())
                .password("NO_PASSWORD")
                .email(oauth2UserInfo.getEmail())
                .imageUrl(oauth2UserInfo.getImageUrl())
                .socialId(oauth2UserInfo.getSocialId())
                .socialType(socialType)
                .role(Role.USER)
                .build();
        memberRepository.save(member);
    }

    private void validateOauthUserInfo(final Oauth2UserInfo oauth2UserInfo) {
        if (oauth2UserInfo.getEmail() == null) {
            OAuth2Error oAuth2Error = new OAuth2Error("401", "소셜 로그인시 이메일은 반드시 존재해야 합니다.", null);
            throw new OAuth2AuthenticationException(oAuth2Error);
        }
    }
}
