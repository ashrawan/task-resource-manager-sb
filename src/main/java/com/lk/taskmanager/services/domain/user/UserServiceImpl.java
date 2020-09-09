package com.lk.taskmanager.services.domain.user;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.repository.UserRepository;
import com.lk.taskmanager.services.domain.user.dtos.UpdatePasswordRequestDTO;
import com.lk.taskmanager.services.generic.GenericSpecification;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import com.lk.taskmanager.utils.MessageCodeUtil;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public GenericResponseDTO<List<UserEntity>> getAllUsers(Pageable pageable) {
        Page<UserEntity> userEntities = userRepository.findAll(pageable);
        return ResponseBuilder.buildPagedSuccessResponse(userEntities);
    }

    @Override
    public GenericResponseDTO<UserEntity> findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return ResponseBuilder.buildSuccessResponse(userEntity);
    }

    @Override
    public GenericResponseDTO<UserEntity> getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return ResponseBuilder.buildSuccessResponse(userEntity);
    }

    @Override
    public GenericResponseDTO<UserEntity> createUser(UserEntity reqUserEntity) {
        UserEntity userEntity = new UserEntity();
        if (reqUserEntity.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(reqUserEntity.getPassword()));
        }
        Optional<UserEntity> usernameAvailable = userRepository.findByUsername(reqUserEntity.getUsername());
        if (usernameAvailable.isPresent()) {
            throw new ResourceNotFoundException(AppExceptionConstants.USER_NAME_NOT_AVAILABLE);
        }
        userEntity.setFullName(reqUserEntity.getFullName());
        userEntity.setPhoneNumber(reqUserEntity.getPhoneNumber());
        userEntity.setRole(reqUserEntity.getRole());
        userEntity.setStatus(reqUserEntity.getStatus());
        userEntity.setUsername(reqUserEntity.getUsername());
        userRepository.save(userEntity);
        return ResponseBuilder.buildSuccessResponse(userEntity);
    }

    @Override
    public GenericResponseDTO<UserEntity> updateUser(UserEntity reqUserEntity) {
        UserEntity userEntity = userRepository.findById(reqUserEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        userEntity.setFullName(reqUserEntity.getFullName());
        userEntity.setPhoneNumber(reqUserEntity.getPhoneNumber());
        userEntity.setRole(reqUserEntity.getRole());
        userEntity.setStatus(reqUserEntity.getStatus());
        userRepository.save(userEntity);
        return ResponseBuilder.buildSuccessResponse(userEntity);
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

    @Override
    public GenericResponseDTO<List<UserEntity>> filterUserData(GenericFilterRequestDTO<UserEntity> genericFilterRequest, Pageable pageable) {
        GenericSpecification<UserEntity> userSpec = UserSearchSpecification.processDynamicUserFilter(genericFilterRequest);
        Page<UserEntity> filteredUsers = userRepository.findAll(userSpec, pageable);
        GenericResponseDTO<List<UserEntity>> genericResponse = ResponseBuilder.buildPagedSuccessResponse(filteredUsers);
        return genericResponse;
    }

}
