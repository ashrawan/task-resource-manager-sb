package com.lk.taskmanager.services.auth;

import com.lk.taskmanager.services.auth.dtos.AuthResponseDTO;
import com.lk.taskmanager.services.auth.dtos.LoginRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;

public interface AuthenticationService {

    GenericResponseDTO<AuthResponseDTO> loginUser(LoginRequestDTO loginRequest);

    GenericResponseDTO<?> forgotPassword(String userEmail);

}
