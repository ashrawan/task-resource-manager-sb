package com.lk.taskmanager.apis;

import com.lk.taskmanager.services.auth.AuthenticationService;
import com.lk.taskmanager.services.auth.dtos.LoginRequestDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Authentication API: login requested by user: ", loginRequest.getUsername());
        return new ResponseEntity<>(authenticationService.loginUser(loginRequest), HttpStatus.OK);
    }

    @GetMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("userEmail") String userEmail) {
        log.info("Authentication API: processing forgot password for email: ", userEmail);
        GenericResponseDTO<?> genericResponse = authenticationService.forgotPassword(userEmail);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }
}
