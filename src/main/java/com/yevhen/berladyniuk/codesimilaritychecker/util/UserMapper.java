package com.yevhen.berladyniuk.codesimilaritychecker.util;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto fromUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mainDirectoryPath(user.getMainDirectoryPath())
                .roles(user.getRoles())
                .build();
    }

}
