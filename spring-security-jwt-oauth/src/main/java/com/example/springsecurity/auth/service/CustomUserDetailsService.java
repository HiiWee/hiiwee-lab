package com.example.springsecurity.auth.service;

import com.example.springsecurity.member.domain.Member;
import com.example.springsecurity.member.repository.MemberRepository;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDetailsService로 사용자가 입력한 로그인 요청과 비교하기 위한 실제 DB의 객체를 조회해서 UserDetails Type으로 넘겨줍니다. UserDetails 인터페이스를 커스텀하게 구성하여
 * 필요한 정보를 더 담을 수 있습니다.
 */
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
