package com.example.springsecurity.member.acceptance;

import static io.restassured.RestAssured.given;

import com.example.springsecurity.member.dto.SignUpRequest;
import com.example.springsecurity.member.dto.SignUpResponse;
import com.example.springsecurity.util.AcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class MemberAcceptanceTest extends AcceptanceTest {


    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void signup() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest("hoseok1", "1234");

        // when
        ExtractableResponse<Response> response = given().log().all()
                .body(signUpRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/members/signup")
                .then().log().all()
                .extract();
        SignUpResponse signUpResponse = response.jsonPath().getObject(".", SignUpResponse.class);

        // then
        Assertions.assertThat(signUpResponse.getMemberId()).isEqualTo(2L);
    }
}
