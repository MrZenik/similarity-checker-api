package com.yevhen.berladyniuk.codesimilaritychecker.service;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.StudentStatistic;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.FilesSimilarPair;

import java.util.List;

public interface FilesSimilarPairService {

    List<StudentStatistic> getStudentStatistic(String studentEmail, Long subjectId, UserDto loggedInUser);

    FilesSimilarPair getByIdAndTeacher(Long id, UserDto loggedInUser);

}
