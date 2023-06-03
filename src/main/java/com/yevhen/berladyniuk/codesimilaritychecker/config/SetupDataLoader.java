package com.yevhen.berladyniuk.codesimilaritychecker.config;

import com.yevhen.berladyniuk.codesimilaritychecker.exception.ApiException;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.RoleRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    @Value("${code-similarity.main-directory}")
    private String mainDirectory;

    @Value("${code-similarity.admin-email}")
    private String adminEmail;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SetupDataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.getRoleByName("ROLE_ADMIN")
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Role not found!"));

            User user = User.builder()
                    .firstName("Main")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin"))
                    .mainDirectoryPath(mainDirectory)
                    .roles(Arrays.asList(adminRole))
                    .isApproved(true)
                    .build();
            userRepository.save(user);
        }
    }

}
