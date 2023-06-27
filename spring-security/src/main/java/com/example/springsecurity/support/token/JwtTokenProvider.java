package com.example.springsecurity.support.token;

import com.example.springsecurity.dto.AuthInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORIZATION_ID = "id";
    private static final String AUTHORIZATION = "auth";

    private final Key signingKey;
    private final long validityMilliseconds;
    private final long refreshTokenValidityMilliseconds;

    public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") final String signingKey,
                            @Value("${security.jwt.token.expire-length.access}") final long validityMilliseconds,
                            @Value("${security.jwt.token.expire-length.refresh}") final long refreshTokenValidityMilliseconds) {
        byte[] keyBytes = signingKey.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.validityMilliseconds = validityMilliseconds;
        this.refreshTokenValidityMilliseconds = refreshTokenValidityMilliseconds;
    }

    public String createAccessToken(final Authentication authentication) {
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityMilliseconds);
        return Jwts.builder()
                .claim(AUTHORIZATION_ID, authentication.getName())
                .claim(AUTHORIZATION, authorities)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey)
                .compact();
    }

    public String createAccessToken(final AuthInfo authInfo) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityMilliseconds);

        return Jwts.builder()
                .claim(AUTHORIZATION_ID, String.valueOf(authInfo.getId()))
                .claim(AUTHORIZATION, (authInfo.getRole() + ","))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityMilliseconds);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey)
                .compact();
    }

    public Authentication getAuthentication(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (claims.get(AUTHORIZATION) == null) {
            throw new IllegalArgumentException("권한 정보가 없습니다.");
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORIZATION)
                        .toString()
                        .split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User((String) claims.get(AUTHORIZATION_ID), "", authorities);

        // Security Context에 담을 Authentication 구현체를 생성합니다. 이때 JWT에 포함되어 있는 정보를 이용해 필요 객체들을 생성합니다.
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public AuthInfo getParsedClaims(final String token) {
        Authentication authentication = getAuthentication(token);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Long id = Long.parseLong(authentication.getName());
        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.equals("USER") || authority.equals("ADMIN"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("사용자의 역할을 찾을 수 없습니다."));

        return new AuthInfo(id, role);
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token : {}", token);
            throw new JwtException("지원하지 않는 JWT 토큰 형식");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token : {}", token);
            throw new JwtException("토큰 기한 만료");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token : {}", token);
            throw new JwtException("유효하지 않은 JWT 토큰");
        } catch (SignatureException e) {
            log.info("Invalid JWT signature : {}", token);
            throw new JwtException("잘못된 JWT 시그니처");
        }
    }
}
