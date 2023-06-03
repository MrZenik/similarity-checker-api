package com.yevhen.berladyniuk.codesimilaritychecker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    private List<Long> studentIds;

    private Long subjectId;

}
