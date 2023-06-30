package com.example.springsecurity.auth.acceptance;

import static com.example.springsecurity.util.fixture.TokenFixture.getMemberToken;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.example.springsecurity.advice.ErrorResponse;
import com.example.springsecurity.auth.dto.ReissuedTokenResponse;
import com.example.springsecurity.auth.dto.SignInRequest;
import com.example.springsecurity.auth.dto.TokenResponse;
import com.example.springsecurity.support.token.JwtTokenProvider;
import com.example.springsecurity.util.AcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AuthAcceptanceTest extends AcceptanceTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @DisplayName("로그인을 할 수 있다.")
    @Test
    void login() {
        // given
        SignInRequest signInRequest = new SignInRequest("hoseok", "1234");

        // when
        ExtractableResponse<Response> response = given().log().all()
                .body(signInRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/signin")
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
        TokenResponse memberToken = getMemberToken();
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

    @DisplayName("로그아웃을 할 수 있다.")
    @Test
    void logout() {
        // given
        TokenResponse memberToken = getMemberToken();
        String memberAccessToken = memberToken.getAccessToken();
        String memberRefreshToken = memberToken.getRefreshToken();
        given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + memberAccessToken)
                .when()
                .get("/signout")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + memberAccessToken)
                .header("Refresh-Token", "Bearer " + memberRefreshToken)
                .when()
                .get("/refresh")
                .then().log().all()
                .extract();
        int statusCode = response.statusCode();
        ErrorResponse errorResponse = response.jsonPath().getObject(".", ErrorResponse.class);

        // then
        assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                () -> assertThat(errorResponse.getMessage()).isEqualTo("유효하지 않은 리프레시 토큰입니다.")
        );
    }
}
