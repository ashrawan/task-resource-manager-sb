package com.lk.taskmanager.services.domain.user;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.domain.user.dtos.UpdatePasswordRequestDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserEntity> getAllUsers(Pageable pageable);

    UserEntity findByUsername(String username);

    UserEntity getUserById(Long id);

    UserEntity createUser(UserEntity userEntity);

    UserEntity updateUser(UserEntity userEntity);

    GenericResponseDTO<?> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest);

}
