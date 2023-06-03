package com.yevhen.berladyniuk.codesimilaritychecker.dto;

import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String mainDirectoryPath;

    private String token;

    Collection<Role> roles;

}
