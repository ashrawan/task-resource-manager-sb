package com.lk.taskmanager.services.auth;

import com.lk.taskmanager.services.auth.dtos.AuthenticationDTO;
import com.lk.taskmanager.services.auth.dtos.LoginRequestDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;

public interface AuthenticationService {

    AuthenticationDTO loginUser(LoginRequestDTO loginRequest);

    GenericResponseDTO<?> forgotPassword(String userEmail);

}
