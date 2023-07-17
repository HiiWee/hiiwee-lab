package com.example.springsecurity.member.repository;

import com.example.springsecurity.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByName(String name);

    Optional<Member> findByName(String name);

    Optional<Member> findByEmailAndSocialId(String email, Long socialId);
}
