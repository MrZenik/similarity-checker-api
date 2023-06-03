package com.yevhen.berladyniuk.codesimilaritychecker.service;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectUpdateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;

import java.util.List;

public interface SubjectService {

    Subject create(SubjectCreateDto subjectCreateDto, UserDto loggedInUser);

    Subject getById(Long id, UserDto loggedInUser);

    List<Subject> getAllByTeacher(Long teacherId);

    Subject updateById(Long id, SubjectUpdateDto subjectUpdateDto, UserDto loggedInUser);

    void deleteById(Long id, UserDto loggedInUser);

}
