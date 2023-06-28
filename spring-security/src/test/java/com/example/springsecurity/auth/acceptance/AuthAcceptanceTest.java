package com.example.springsecurity.auth.acceptance;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.example.springsecurity.auth.dto.ReissuedTokenResponse;
import com.example.springsecurity.auth.dto.SignInRequest;
import com.example.springsecurity.auth.dto.TokenResponse;
import com.example.springsecurity.support.token.JwtTokenProvider;
import com.example.springsecurity.util.AcceptanceTest;
import com.example.springsecurity.util.fixture.TokenFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class AuthAcceptanceTest extends AcceptanceTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @DisplayName("로그인을 할 수 있다.")
    @Test
    void signIn() {
        // given
        SignInRequest signInRequest = new SignInRequest("hoseok", "1234");

        // when
        ExtractableResponse<Response> response = given().log().all()
                .body(signInRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/auth/signin")
                .then().log().all()
                .extract();
        TokenResponse tokenResponse = response.jsonPath().getObject(".", TokenResponse.class);
        boolean accessTokenValidResult = jwtTokenProvider.validateToken(tokenResponse.getAccessToken());
        boolean refreshTokenValidResult = jwtTokenProvider.validateToken(tokenResponse.getAccessToken());

        // then
        assertAll(
                () -> assertThat(accessTokenValidResult).isTrue(),
                () -> assertThat(refreshTokenValidResult).isTrue()
        );
    }

    @DisplayName("토큰을 재발급 받을 수 있다.")
    @Test
    void refresh() {
        // given
        TokenResponse memberToken = TokenFixture.getMemberToken();
        String memberAccessToken = memberToken.getAccessToken();
        String memberRefreshToken = memberToken.getRefreshToken();

        // when
        ExtractableResponse<Response> response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + memberAccessToken)
                .header("Refresh-Token", "Bearer " + memberRefreshToken)
                .when()
                .get("/refresh")
                .then().log().all()
                .extract();
        ReissuedTokenResponse reissuedTokenResponse = response.jsonPath().getObject(".", ReissuedTokenResponse.class);
        boolean reissuedTokenValidResult = jwtTokenProvider.validateToken(reissuedTokenResponse.getAccessToken());

        // then
        assertAll(
                () -> assertThat(reissuedTokenValidResult).isTrue(),
                () -> assertThat(memberAccessToken.equals(reissuedTokenResponse.getAccessToken())).isFalse()
        );
    }
}
