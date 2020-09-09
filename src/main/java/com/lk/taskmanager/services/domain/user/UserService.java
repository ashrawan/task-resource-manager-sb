package com.lk.taskmanager.services.domain.user;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.domain.user.dtos.UpdatePasswordRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    GenericResponseDTO<List<UserEntity>> getAllUsers(Pageable pageable);

    GenericResponseDTO<UserEntity> findByUsername(String username);

    GenericResponseDTO<UserEntity> getUserById(Long id);

    GenericResponseDTO<UserEntity> createUser(UserEntity userEntity);

    GenericResponseDTO<UserEntity> updateUser(UserEntity userEntity);

    GenericResponseDTO<?> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest);

    GenericResponseDTO<List<UserEntity>> filterUserData(GenericFilterRequestDTO<UserEntity> genericFilterRequestDTO, Pageable pageable);


}
