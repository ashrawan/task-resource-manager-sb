package com.lk.taskmanager.apis;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.domain.user.UserService;
import com.lk.taskmanager.services.domain.user.dtos.UpdatePasswordRequestDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<?> getAllUser(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        log.info("User API: get all user");
        return new ResponseEntity<>(userService.getAllUsers(pageable), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("User API: get user by id: ", id);
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity) {
        log.info("User API: create user");
        return new ResponseEntity<>(userService.createUser(userEntity), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserEntity userEntity) {
        log.info("User API: update user");
        return new ResponseEntity<>(userService.updateUser(userEntity), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordRequestDTO updatePasswordRequest) {
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
        UserEntity userEntity = userService.getUserById(authenticatedUserId);
        return new ResponseEntity<>(userEntity, HttpStatus.OK);
    }

}
