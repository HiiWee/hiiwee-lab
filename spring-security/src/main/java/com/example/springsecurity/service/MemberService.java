package com.example.springsecurity.service;

import com.example.springsecurity.domain.Member;
import com.example.springsecurity.domain.Role;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.respository.MemberRepository;
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
    public Long signup(final SignUpRequest signUpRequest) {
        if (memberRepository.existsByName(signUpRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        Member member = new Member(signUpRequest.getName(), passwordEncoder.encode(signUpRequest.getPassword()),
                Role.USER);
        memberRepository.save(member);
        return member.getId();
    }
}
