package com.yevhen.berladyniuk.codesimilaritychecker.services;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.EnrollmentRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.SubjectRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.impl.EnrollmentServiceImpl;
import com.yevhen.berladyniuk.codesimilaritychecker.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EnrollmentServiceImplTest {


    @Mock
    UserRepository userRepository;

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    EnrollmentServiceImpl enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void enroll() {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setSubjectId(1L);
        enrollmentRequest.setStudentIds(Collections.singletonList(1L));

        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        Subject subject = new Subject();
        subject.setTeacherId(1L);

        when(subjectRepository.findById(anyLong())).thenReturn(java.util.Optional.of(subject));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(enrollmentRepository.existsByStudentIdAndSubjectId(anyLong(), anyLong())).thenReturn(false);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));

        enrollmentService.enroll(enrollmentRequest, loggedInUser);

        verify(subjectRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(enrollmentRepository, times(1)).existsByStudentIdAndSubjectId(anyLong(), anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(enrollmentRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getAllStudentSubjectsById() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        enrollmentService.getAllStudentSubjectsById(1L, loggedInUser);

        verify(enrollmentRepository, times(1)).findAllSubjectsByUserId(anyLong());
    }

    @Test
    void getAllStudentsBySubjectId() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        when(subjectRepository.existsByIdAndTeacherId(anyLong(), anyLong())).thenReturn(true);
        when(enrollmentRepository.findUsersBySubjectId(anyLong())).thenReturn(Collections.singletonList(new User()));

        enrollmentService.getAllStudentsBySubjectId(1L, loggedInUser);

        verify(subjectRepository, times(1)).existsByIdAndTeacherId(anyLong(), anyLong());
        verify(enrollmentRepository, times(1)).findUsersBySubjectId(anyLong());
        verify(userMapper, times(1)).fromUserToDto(any(User.class));
    }

}
