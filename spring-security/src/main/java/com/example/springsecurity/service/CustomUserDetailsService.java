package com.example.springsecurity.service;

import com.example.springsecurity.domain.Member;
import com.example.springsecurity.respository.MemberRepository;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // AuthService에 의해 호출됩니다.
    @Override
    public UserDetails loadUserByUsername(final String name) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(member.getRole().toString());
        return User.builder()
                // UserDetails의 구현체인 User에 id 값을 저장함
                .username(String.valueOf(member.getId()))
                .password(member.getPassword())
                .authorities(authorities)
                .build();
    }
}
