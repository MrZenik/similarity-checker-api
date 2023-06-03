package com.yevhen.berladyniuk.codesimilaritychecker.service.impl;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.LoginRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserUpdateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.exception.ApiException;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.RoleRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.UserService;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import com.yevhen.berladyniuk.codesimilaritychecker.util.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DirectoryManager directoryManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           EnrollmentRepository enrollmentRepository,
                           DirectoryManager directoryManager,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.directoryManager = directoryManager;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getById(Long id, UserDto loggedInUser) {
        this.checkIfTheSamePersonId(id, loggedInUser.getId());
        return userMapper.fromUserToDto(getByIdOrElseThrow(id));
    }

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        RandomStringUtils.randomAlphanumeric(4);
        Role role = this.getRoleByNameOrElseThrow(userCreateDto.getRoleName());

        String mainDirectoryPath = null;
        boolean isApproved = true;
        if ("ROLE_TEACHER".equals(role.getName())) {
            mainDirectoryPath = generateDirectoryName(userCreateDto.getEmail());
            isApproved = false;
        }

        User savedUser = userRepository.save(User.builder()
                .firstName(userCreateDto.getFirstName())
                .lastName(userCreateDto.getLastName())
                .email(userCreateDto.getEmail())
                .password(passwordEncoder.encode(userCreateDto.getPassword()))
                .mainDirectoryPath(mainDirectoryPath)
                .roles(Arrays.asList(role))
                .isApproved(isApproved)
                .build());

        if ("ROLE_TEACHER".equals(role.getName())) {
            directoryManager.createDirectory(savedUser.getMainDirectoryPath());
            directoryManager.createDirectory(savedUser.getMainDirectoryPath() + "/report");
        }
        return userMapper.fromUserToDto(savedUser);
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "User not found by email!"));
        if (user.isApproved()) {
            return userMapper.fromUserToDto(user);
        }
        throw new ApiException(HttpStatus.CONFLICT, "User is not active!");
    }

    @Override
    public Collection<? extends GrantedAuthority> getUserRolesByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "User not found by email!"));

        List<GrantedAuthority> authorities = new ArrayList<>();
        Collection<Role> roles = user.getRoles();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public UserDto authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "User not found by email!"));

        if (!user.isApproved()) {
            throw new ApiException(HttpStatus.CONFLICT, "User is not active!");
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return userMapper.fromUserToDto(user);
        }

        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid password!");
    }

    @Override
    public UserDto updateById(Long id, UserUpdateDto userUpdateDto, UserDto loggedInUser) {
        this.checkIfTheSamePersonId(id, loggedInUser.getId());
        User user = this.getByIdOrElseThrow(id);

        if (Objects.isNull(userUpdateDto.getOldPassword()) && Objects.isNull(userUpdateDto.getNewPassword())) {
            User savedUser = userRepository.save(user.toBuilder()
                    .firstName(userUpdateDto.getFirstName())
                    .lastName(userUpdateDto.getLastName())
                    .build());

            return userMapper.fromUserToDto(savedUser);
        }

        if (Objects.isNull(userUpdateDto.getOldPassword()) || Objects.isNull(userUpdateDto.getNewPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Old or new passwords are null!");
        }

        if (passwordEncoder.matches(userUpdateDto.getOldPassword(), userUpdateDto.getNewPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The new password is the same as the user's current password!");
        }

        if (!passwordEncoder.matches(userUpdateDto.getOldPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Old password does not match current user password!");
        }

        User savedUser = userRepository.save(user.toBuilder()
                .firstName(userUpdateDto.getFirstName())
                .lastName(userUpdateDto.getLastName())
                .password(passwordEncoder.encode(userUpdateDto.getNewPassword()))
                .build());

        return userMapper.fromUserToDto(savedUser);
    }

    @Override
    public boolean deleteById(Long id, UserDto loggedInUser) {
        this.checkIfTheSamePersonId(id, loggedInUser.getId());
        User user = this.getByIdOrElseThrow(id);

        this.directoryManager.deleteDirectory(user.getMainDirectoryPath());
        this.enrollmentRepository.deleteAllByStudentId(id);
        this.userRepository.deleteById(user.getId());
        return !userRepository.existsById(user.getId());
    }

    @Override
    public List<UserDto> findUsersByEmail(String email) {
        return this.userRepository.findAllByEmailContainsAndRoles_Name(email, "ROLE_STUDENT")
                .stream()
                .map(userMapper::fromUserToDto)
                .toList();
    }

    private String generateDirectoryName(String email) {
        return email.substring(0, email.indexOf("@")) + "_" + RandomStringUtils.randomAlphanumeric(4);
    }

    private User getByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found!"));
    }

    private Role getRoleByNameOrElseThrow(String roleName) {
        return roleRepository.getRoleByName(roleName)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Role not found!"));
    }

    private void checkIfTheSamePersonId(Long id, Long loggedInId) {
        if (!loggedInId.equals(id)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

}
