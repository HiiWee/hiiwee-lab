package com.example.springsecurity.util.fixture;

import static io.restassured.RestAssured.given;

import com.example.springsecurity.auth.dto.SignInRequest;
import com.example.springsecurity.auth.dto.TokenResponse;
import org.springframework.http.MediaType;

public class TokenFixture {


    public static TokenResponse getMemberToken() {
        SignInRequest signInRequest = new SignInRequest("hoseok", "1234");
        return given().log().all()
                .body(signInRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/auth/signin")
                .then().log().all()
                .extract()
                .jsonPath()
                .getObject(".", TokenResponse.class);
    }
}
