package com.lk.taskmanager.services.auth;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.auth.dtos.AuthUserDTO;

public class AuthUserMapper {

    public static AuthUserDTO mapToAuthUserDTO(UserEntity userEntity) {
        AuthUserDTO authUser = new AuthUserDTO();
        authUser.setId(userEntity.getId());
        authUser.setFullName(userEntity.getFullName());
        authUser.setUsername(userEntity.getUsername());
        authUser.setRole(userEntity.getRole());
        authUser.setPhoneNumber(userEntity.getPhoneNumber());
        authUser.setStatus(userEntity.getStatus());
        return authUser;
    }

    public static UserEntity mapToAuthUserEntity(AuthUserDTO authUser) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(authUser.getId());
        userEntity.setFullName(authUser.getFullName());
        userEntity.setUsername(authUser.getUsername());
        userEntity.setRole(authUser.getRole());
        userEntity.setPhoneNumber(authUser.getPhoneNumber());
        userEntity.setStatus(authUser.getStatus());
        return userEntity;
    }
}
