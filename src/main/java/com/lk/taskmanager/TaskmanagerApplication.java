package com.lk.taskmanager;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.repository.UserRepository;
import com.lk.taskmanager.utils.Enums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@SpringBootApplication
public class TaskmanagerApplication implements CommandLineRunner {

    @Value("${app.security.initial.admin-username:admin}")
    private String initialAdminUsername;

    @Value("${app.security.initial.admin-pass:admin}")
    private String initialAdminPass;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public TaskmanagerApplication(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(TaskmanagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<UserEntity> admin = userRepository.findByUsername(initialAdminUsername);
        if(!admin.isPresent()){
            log.warn("Initial Admin-User Not Found, initializing default admin with username: ", initialAdminUsername);
            String initialAdminPassword = passwordEncoder.encode(initialAdminPass);
            UserEntity defaultAdmin = new UserEntity();
            defaultAdmin.setUsername(initialAdminUsername);
            defaultAdmin.setFullName(initialAdminUsername);
            defaultAdmin.setPassword(initialAdminPassword);
            defaultAdmin.setRole(String.valueOf(Enums.UserRoleStatus.ROLE_ADMIN));
            defaultAdmin.setStatus(Enums.UserStatus.ACTIVE);
            userRepository.save(defaultAdmin);
            log.info("Default Admin-User Initialization Successful");
        }
    }

}
