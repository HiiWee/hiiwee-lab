package com.example.springsecurity.respository;

import com.example.springsecurity.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByMemberId(Long memberId);

}
