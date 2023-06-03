package com.yevhen.berladyniuk.codesimilaritychecker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilesSimilarPairDto {

    private Long id;
    private String file2;
    private double similarityScore;

}
