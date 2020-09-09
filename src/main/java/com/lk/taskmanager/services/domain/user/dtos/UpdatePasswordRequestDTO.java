package com.lk.taskmanager.services.domain.user.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePasswordRequestDTO {

    private Long userId;

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}
