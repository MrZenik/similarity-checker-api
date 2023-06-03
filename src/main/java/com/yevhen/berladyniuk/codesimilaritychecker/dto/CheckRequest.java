package com.yevhen.berladyniuk.codesimilaritychecker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckRequest {

    @NotEmpty
    private List<String> testDirectories;

    @NotEmpty
    private List<String> refDirectories;

    @NotBlank
    @NotNull
    private String reportOutput;

    private int noiseThreshold;

    private int guaranteeThreshold;

    private double displayThreshold;

    private boolean removeImports;

    private boolean skipPunctuation;

    private boolean truncate;

    private boolean disableFilter;

}
