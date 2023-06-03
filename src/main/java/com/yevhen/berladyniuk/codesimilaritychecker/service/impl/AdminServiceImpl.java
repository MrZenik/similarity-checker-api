package com.yevhen.berladyniuk.codesimilaritychecker.service.impl;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.exception.ApiException;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.RoleRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.AdminService;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import com.yevhen.berladyniuk.codesimilaritychecker.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DirectoryManager directoryManager;
    private final UserMapper userMapper;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository, RoleRepository roleRepository, DirectoryManager directoryManager, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.directoryManager = directoryManager;
        this.userMapper = userMapper;
    }

    @Override
    public void approveTeacherStatusByUserId(Long userId) {
        User user = this.getByIdOrElseThrow(userId);
        user.setApproved(true);
        userRepository.save(user);
    }

    @Override
    public void setStudentRoleByUserId(Long userId) {
        User user = this.getByIdOrElseThrow(userId);
        Role role = this.getRoleByNameOrElseThrow("ROLE_STUDENT");

        String directory = user.getMainDirectoryPath();

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        user.setApproved(true);
        user.setMainDirectoryPath(null);

        userRepository.save(user);
        this.directoryManager.deleteDirectory(directory);
    }

    @Override
    public List<UserDto> getWaitingForApprove() {
        return userRepository.getAllByIsApprovedIsFalse().stream().map(userMapper::fromUserToDto).toList();
    }

    @Override
    public boolean deleteUserAccountById(Long id) {
        User user = this.getByIdOrElseThrow(id);

        this.directoryManager.deleteDirectory(user.getMainDirectoryPath());
        this.userRepository.deleteById(user.getId());

        return !userRepository.existsById(user.getId());
    }

    private User getByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found!"));
    }

    private Role getRoleByNameOrElseThrow(String roleName) {
        return roleRepository.getRoleByName(roleName)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Role not found!"));
    }

}
