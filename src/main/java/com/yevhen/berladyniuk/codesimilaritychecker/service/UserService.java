package com.yevhen.berladyniuk.codesimilaritychecker.service;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.LoginRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserUpdateDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto getById(Long id, UserDto loggedInUser);

    UserDto create(UserCreateDto userCreateDto);

    UserDto findByEmail(String email);

    Collection<? extends GrantedAuthority> getUserRolesByEmail(String email);

    UserDto authenticate(LoginRequest loginRequest);

    UserDto updateById(Long id, UserUpdateDto userUpdateDto, UserDto loggedInUser);

    boolean deleteById(Long id, UserDto loggedInUser);

    List<UserDto> findUsersByEmail(String email);

}
