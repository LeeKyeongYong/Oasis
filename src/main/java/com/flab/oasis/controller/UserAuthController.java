package com.flab.oasis.controller;

import com.flab.oasis.model.GoogleOAuthLoginRequest;
import com.flab.oasis.model.LoginResult;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/login/default")
    public boolean loginAuthFromUserLoginRequest(@RequestBody UserLoginRequest userLoginRequest) {
        setSecurityContext(
                userLoginRequest.getUid(),
                userAuthService.tryLoginDefault(userLoginRequest)
        );

        return true;
    }

    @PostMapping("/login/google")
    public void loginGoogleByGoogleOAuthToken(@RequestBody GoogleOAuthLoginRequest googleOAuthLoginRequest) {
        LoginResult loginResult = userAuthService.tryLoginGoogle(
                googleOAuthLoginRequest
        );

        setSecurityContext(loginResult.getUid(), loginResult);
    }

    private boolean setSecurityContext(String uid, LoginResult loginResult) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                uid, loginResult, new ArrayList<>()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return loginResult.isJoinUser();
    }
}
