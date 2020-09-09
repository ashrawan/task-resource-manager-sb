package com.lk.taskmanager.services.auth;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.security.JwtTokenProvider;
import com.lk.taskmanager.services.auth.dtos.AuthResponseDTO;
import com.lk.taskmanager.services.auth.dtos.AuthUserDTO;
import com.lk.taskmanager.services.auth.dtos.LoginRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import com.lk.taskmanager.utils.MessageCodeUtil;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public GenericResponseDTO<AuthResponseDTO> loginUser(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserEntity userEntity = (UserEntity) authentication.getPrincipal();
            AuthUserDTO authUserDTO = AuthUserMapper.mapToAuthUserDTO(userEntity);
            String token = jwtTokenProvider.createToken(authUserDTO);
            AuthResponseDTO authenticationDTO = new AuthResponseDTO(token, AuthUserMapper.mapToAuthUserDTO(userEntity));
            return ResponseBuilder.buildSuccessResponse(authenticationDTO);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException(AppExceptionConstants.BAD_LOGIN_CREDENTIALS);
        }
    }

    @Override
    public GenericResponseDTO<?> forgotPassword(String userEmail) {
        return ResponseBuilder.buildFailureResponse(MessageCodeUtil.IMPLEMENTATION_NOT_AVAILABLE);
    }
}
