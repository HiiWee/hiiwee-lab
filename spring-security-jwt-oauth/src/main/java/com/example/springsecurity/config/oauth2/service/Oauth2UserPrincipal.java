package com.example.springsecurity.config.oauth2.service;

import java.util.Collection;
import java.util.Map;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Oauth2UserPrincipal implements OAuth2User {

    private Long memberId;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    @Builder
    private Oauth2UserPrincipal(final Long memberId, final Map<String, Object> attributes,
                                final Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
