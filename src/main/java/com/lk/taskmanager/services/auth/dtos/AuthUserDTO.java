package com.lk.taskmanager.services.auth.dtos;

import com.lk.taskmanager.utils.Enums;
import lombok.Data;

@Data
public class AuthUserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String role;
    private String phoneNumber;
    private Enums.UserStatus status;
}
