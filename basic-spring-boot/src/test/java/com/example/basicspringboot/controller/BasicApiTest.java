package com.example.basicspringboot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BasicApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUpDatabase() {
        RestAssured.port = port;
    }

    @DisplayName("mapper write 이후 status를 set하면 적용되지 않는다.")
    @Test
    void afterWriteToMapper_statusWillNeverChange() {
        // given & when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.TEXT)
                .when()
                .get("/basic")
                .then().log().all()
                .extract();
        MessageResponse messageResponse = response.body().jsonPath().getObject(".", MessageResponse.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(messageResponse.getMessage()).isEqualTo("response message to Response Body.")
        );
    }
}
