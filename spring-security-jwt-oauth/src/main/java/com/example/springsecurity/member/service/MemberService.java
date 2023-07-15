package com.example.springsecurity.member.service;

import com.example.springsecurity.member.domain.Member;
import com.example.springsecurity.member.domain.Role;
import com.example.springsecurity.member.domain.SocialType;
import com.example.springsecurity.member.dto.SignUpRequest;
import com.example.springsecurity.member.dto.SignUpResponse;
import com.example.springsecurity.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignUpResponse signup(final SignUpRequest signUpRequest) {
        if (memberRepository.existsByName(signUpRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        Member member = Member.builder()
                .name(signUpRequest.getName())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .socialType(SocialType.NONE)
                .role(Role.USER)
                .build();
        memberRepository.save(member);
        return new SignUpResponse(member.getId());
    }
}
