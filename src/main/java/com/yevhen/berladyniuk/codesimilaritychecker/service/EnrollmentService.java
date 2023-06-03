package com.yevhen.berladyniuk.codesimilaritychecker.service;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.EnrollmentRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;

import java.util.List;

public interface EnrollmentService {

    void enroll(EnrollmentRequest enrollmentRequest, UserDto loggedInUser);

    List<Subject> getAllStudentSubjectsById(Long id, UserDto loggedInUser);

    List<UserDto> getAllStudentsBySubjectId(Long id, UserDto loggedInUser);

}
