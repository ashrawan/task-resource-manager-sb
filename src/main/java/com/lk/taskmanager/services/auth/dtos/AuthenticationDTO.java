package com.lk.taskmanager.services.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationDTO {

    private String username;

    private String token;
}