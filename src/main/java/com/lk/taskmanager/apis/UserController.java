package com.lk.taskmanager.apis;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.domain.user.UserService;
import com.lk.taskmanager.services.domain.user.dtos.UpdatePasswordRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("rest/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public ResponseEntity<?> getAllUser(Pageable pageable) {
        log.info("User API: get all user");
        GenericResponseDTO<List<UserEntity>> genericResponse = userService.getAllUsers(pageable);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("User API: get user by id: ", id);
        GenericResponseDTO<UserEntity> genericResponse = userService.getUserById(id);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity) {
        log.info("User API: create user");
        GenericResponseDTO<UserEntity> genericResponse = userService.createUser(userEntity);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserEntity userEntity) {
        log.info("User API: update user");
        GenericResponseDTO<UserEntity> genericResponse = userService.updateUser(userEntity);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequest) {
        Long userId = ExtractAuthUser.resolveUserId(updatePasswordRequest.getUserId());
        log.info("User API: processing password update for userId: ", userId);
        updatePasswordRequest.setUserId(userId);
        GenericResponseDTO<?> genericResponse = userService.updatePassword(updatePasswordRequest);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<?> retrieveAuthenticatedUser() {
        Long authenticatedUserId = ExtractAuthUser.resolveUserId(null);
        log.info("User API: retrieve authenticated user details for userId: ", authenticatedUserId);
        GenericResponseDTO<UserEntity> genericResponse = userService.getUserById(authenticatedUserId);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/filter")
    public ResponseEntity<?> filterUserData(@RequestBody @Valid GenericFilterRequestDTO<UserEntity> genericFilterRequest, Pageable pageable) {
        log.info("User API: Filter user data");
        GenericResponseDTO<?> genericResponse = userService.filterUserData(genericFilterRequest, pageable);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

}
