package com.example.springsecurity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringSecurityApplicationTests {

    @Test
    void contextLoads() {
    }

    enum A {
        AB, CD
    }


    @Test
    void enumName() {
        System.out.println(A.AB.name());
    }

}
