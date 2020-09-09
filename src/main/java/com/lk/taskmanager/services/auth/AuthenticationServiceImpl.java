package com.lk.taskmanager.services.auth;

import com.lk.taskmanager.security.JwtTokenProvider;
import com.lk.taskmanager.services.auth.dtos.AuthenticationDTO;
import com.lk.taskmanager.services.auth.dtos.LoginRequestDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import com.lk.taskmanager.services.generic.MessageCodeUtil;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthenticationDTO loginUser(LoginRequestDTO loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            String token = jwtTokenProvider.createToken(loginRequest.getUsername());
            AuthenticationDTO authenticationDTO = new AuthenticationDTO(loginRequest.getUsername(), token);
            return authenticationDTO;
        } catch (AuthenticationException e) {
            throw new BadCredentialsException(AppExceptionConstants.BAD_LOGIN_CREDENTIALS);
        }
    }

    @Override
    public GenericResponseDTO<?> forgotPassword(String userEmail) {
        return ResponseBuilder.buildFailureResponse(MessageCodeUtil.IMPLEMENTATION_NOT_AVAILABLE);
    }
}
