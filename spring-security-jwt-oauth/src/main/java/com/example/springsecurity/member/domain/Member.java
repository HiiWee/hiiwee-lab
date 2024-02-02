package com.example.springsecurity.member.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String password;

    private String email;

    private String imageUrl;

    private Long socialId;

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    protected Member() {
    }

    @Builder
    private Member(final String name, final String password, final String email, final String imageUrl,
                   final Long socialId, final SocialType socialType, final Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.imageUrl = imageUrl;
        this.socialId = socialId;
        this.socialType = socialType;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role.getValue();
    }

    public void updateName(final String name) {
        this.name = name;
    }

    public void updateImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }
}