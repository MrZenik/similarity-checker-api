package com.yevhen.berladyniuk.codesimilaritychecker.controller;

import com.yevhen.berladyniuk.codesimilaritychecker.config.UserAuthenticationProvider;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.LoginRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserUpdateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    public UserController(UserService userService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id, @AuthenticationPrincipal UserDto loggedInUser) {
        return userService.getById(id, loggedInUser);
    }

    @GetMapping("/find-users")
    public List<UserDto> findUsersByEmail(@RequestParam String email) {
        return userService.findUsersByEmail(email);
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @PostMapping("/login")
    public UserDto signIn(@AuthenticationPrincipal UserDto userDto) {
        userDto.setToken(userAuthenticationProvider.createToken(userDto));
        return userDto;
    }

    @PutMapping("/{id}")
    public UserDto updateById(@PathVariable Long id,
                              @RequestBody UserUpdateDto userUpdateDto,
                              @AuthenticationPrincipal UserDto loggedInUser
    ) {
        return userService.updateById(id, userUpdateDto, loggedInUser);
    }

    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable Long id,
                              @AuthenticationPrincipal UserDto loggedInUser
    ) {
        return userService.deleteById(id, loggedInUser);
    }

}
