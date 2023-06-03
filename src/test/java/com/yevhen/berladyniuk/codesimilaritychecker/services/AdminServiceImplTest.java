package com.yevhen.berladyniuk.codesimilaritychecker.services;


import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.RoleRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.impl.AdminServiceImpl;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import com.yevhen.berladyniuk.codesimilaritychecker.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    DirectoryManager directoryManager;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void approveTeacherStatusByUserId() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));

        adminService.approveTeacherStatusByUserId(1L);

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void setStudentRoleByUserId() {
        User user = new User();
        user.setMainDirectoryPath("path");

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));
        when(roleRepository.getRoleByName(anyString())).thenReturn(java.util.Optional.of(new Role()));

        adminService.setStudentRoleByUserId(1L);

        verify(userRepository, times(1)).findById(anyLong());
        verify(roleRepository, times(1)).getRoleByName(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(directoryManager, times(1)).deleteDirectory(anyString());
    }

    @Test
    void getWaitingForApprove() {
        when(userRepository.getAllByIsApprovedIsFalse()).thenReturn(Collections.singletonList(new User()));

        adminService.getWaitingForApprove();

        verify(userRepository, times(1)).getAllByIsApprovedIsFalse();
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

    @Test
    void deleteUserAccountById() {
        User user = new User();
        user.setId(1L);
        user.setMainDirectoryPath("path");

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));

        adminService.deleteUserAccountById(1L);

        verify(userRepository, times(1)).findById(anyLong());
        verify(directoryManager, times(1)).deleteDirectory(anyString());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}