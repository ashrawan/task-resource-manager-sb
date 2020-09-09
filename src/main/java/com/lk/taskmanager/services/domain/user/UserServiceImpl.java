package com.lk.taskmanager.services.domain.user;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.repository.UserRepository;
import com.lk.taskmanager.services.domain.user.dtos.UpdatePasswordRequestDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import com.lk.taskmanager.services.generic.MessageCodeUtil;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.utils.Enums;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UserEntity> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public UserEntity findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return userEntity;
    }

    @Override
    public UserEntity getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return userEntity;
    }

    @Override
    public UserEntity createUser(UserEntity reqUserEntity) {
        UserEntity userEntity = new UserEntity();
        if (userEntity.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(reqUserEntity.getPassword()));
        }
        userEntity.setFullName(reqUserEntity.getFullName());
        userEntity.setPhoneNumber(reqUserEntity.getPhoneNumber());
        userEntity.setRole(reqUserEntity.getRole());
        userEntity.setStatus(Enums.UserStatus.INACTIVE);
        userEntity.setUsername(reqUserEntity.getUsername());
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUser(UserEntity reqUserEntity) {
        UserEntity userEntity = userRepository.findById(reqUserEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        userEntity.setFullName(reqUserEntity.getFullName());
        userEntity.setPhoneNumber(reqUserEntity.getPhoneNumber());
        userEntity.setRole(reqUserEntity.getRole());
        userEntity.setStatus(reqUserEntity.getStatus());
        return userRepository.save(userEntity);
    }

    @Override
    public GenericResponseDTO<?> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest) {
        UserEntity userEntity = userRepository.findById(updatePasswordRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        boolean passwordMatches = passwordEncoder.matches(updatePasswordRequest.getOldPassword(), userEntity.getPassword());
        if (!passwordMatches) {
            return ResponseBuilder.buildResponse(MessageCodeUtil.CONFLICT, AppExceptionConstants.OLD_PASSWORD_DOESNT_MATCH);
        }
        userEntity.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(userEntity);
        return ResponseBuilder.buildSuccessResponse(null);
    }

}
