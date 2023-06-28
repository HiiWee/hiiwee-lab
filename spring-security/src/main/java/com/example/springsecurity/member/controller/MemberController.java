package com.example.springsecurity.member.controller;

import com.example.springsecurity.member.dto.SignUpRequest;
import com.example.springsecurity.member.dto.SignUpResponse;
import com.example.springsecurity.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody final SignUpRequest signUpRequest) {
        Long memberId = memberService.signup(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignUpResponse(memberId));
    }
}
