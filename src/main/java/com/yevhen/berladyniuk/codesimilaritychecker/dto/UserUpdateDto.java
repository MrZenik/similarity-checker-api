package com.yevhen.berladyniuk.codesimilaritychecker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

}