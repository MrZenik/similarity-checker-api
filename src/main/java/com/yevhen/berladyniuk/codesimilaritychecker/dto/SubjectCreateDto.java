package com.yevhen.berladyniuk.codesimilaritychecker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectCreateDto {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String folderName;

}
