package com.example.springsecurity.member.repository;

import com.example.springsecurity.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByName(String name);

    Optional<Member> findByName(String name);

    boolean existsByEmailAndSocialId(String email, Long socialId);

    Optional<Member> findByEmail(String email);
}
