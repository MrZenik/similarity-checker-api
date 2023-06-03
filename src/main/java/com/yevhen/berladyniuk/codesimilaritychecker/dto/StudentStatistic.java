package com.yevhen.berladyniuk.codesimilaritychecker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentStatistic {

    private String file1;
    private List<FilesSimilarPairDto> pairs;

}
