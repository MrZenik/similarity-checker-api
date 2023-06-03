package com.yevhen.berladyniuk.codesimilaritychecker.services;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.LoginRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserUpdateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.RoleRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.impl.UserServiceImpl;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import com.yevhen.berladyniuk.codesimilaritychecker.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    DirectoryManager directoryManager;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void create() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("test@test.com");
        userCreateDto.setFirstName("Test");
        userCreateDto.setLastName("User");
        userCreateDto.setPassword("password");
        userCreateDto.setRoleName("ROLE_USER");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.getRoleByName(anyString())).thenReturn(java.util.Optional.of(new Role()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userMapper.fromUserToDto(any(User.class))).thenReturn(null);

        userService.create(userCreateDto);

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(roleRepository, times(1)).getRoleByName(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

    @Test
    void getById() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(userMapper.fromUserToDto(any(User.class))).thenReturn(null);

        userService.getById(1L, loggedInUser);

        verify(userRepository, times(1)).findById(anyLong());
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

    @Test
    void findByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(User.builder().isApproved(true).build()));

        userService.findByEmail("test@test.com");

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

    @Test
    void authenticate() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");

        User user = new User();
        user.setApproved(true);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        userService.authenticate(loginRequest);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

    @Test
    void deleteById() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setMainDirectoryPath("path");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deleteById(1L, loggedInUser);

        verify(userRepository, times(1)).findById(anyLong());
        verify(directoryManager, times(1)).deleteDirectory(anyString());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void getUserRolesByEmail() {
        User user = new User();
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(Collections.singletonList(role));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        userService.getUserRolesByEmail("test@test.com");

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void updateById() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("Updated");
        userUpdateDto.setLastName("User");
        userUpdateDto.setOldPassword("oldPassword");
        userUpdateDto.setNewPassword("newPassword");

        User user = new User();
        user.setPassword("oldPassword");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "oldPassword")).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.updateById(1L, userUpdateDto, loggedInUser);

        verify(userRepository, times(1)).findById(anyLong());
        verify(passwordEncoder, times(2)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

    @Test
    void findUsersByEmail() {
        List<User> users = new ArrayList<>();
        users.add(new User());

        when(userRepository.findAllByEmailContainsAndRoles_Name(anyString(), anyString())).thenReturn(users);

        userService.findUsersByEmail("test@test.com");

        verify(userRepository, times(1)).findAllByEmailContainsAndRoles_Name(anyString(), anyString());
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

}