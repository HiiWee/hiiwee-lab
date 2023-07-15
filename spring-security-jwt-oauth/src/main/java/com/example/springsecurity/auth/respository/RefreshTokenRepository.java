package com.example.springsecurity.auth.respository;

import com.example.springsecurity.auth.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteAllByMemberId(Long memberId);

    Optional<RefreshToken> findByMemberId(Long memberId);
}
