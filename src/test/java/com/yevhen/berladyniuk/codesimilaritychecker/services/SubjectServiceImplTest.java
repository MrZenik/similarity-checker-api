package com.yevhen.berladyniuk.codesimilaritychecker.services;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectUpdateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.SubjectRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.impl.SubjectServiceImpl;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class SubjectServiceImplTest {

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    DirectoryManager directoryManager;

    @InjectMocks
    SubjectServiceImpl subjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void create() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        SubjectCreateDto subjectCreateDto = new SubjectCreateDto();
        subjectCreateDto.setName("name");
        subjectCreateDto.setFolderName("folder");

        subjectService.create(subjectCreateDto, userDto);

        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void getById() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        when(subjectRepository.existsByIdAndTeacherId(anyLong(), anyLong())).thenReturn(true);
        when(subjectRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Subject()));

        subjectService.getById(1L, loggedInUser);

        verify(subjectRepository, times(1)).existsByIdAndTeacherId(anyLong(), anyLong());
        verify(subjectRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllByTeacher() {
        subjectService.getAllByTeacher(1L);

        verify(subjectRepository, times(1)).findAllByTeacherId(anyLong());
    }

    @Test
    void updateById() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        Subject subject = new Subject();
        subject.setTeacherId(1L);

        SubjectUpdateDto subjectUpdateDto = new SubjectUpdateDto();
        subjectUpdateDto.setName("new name");

        when(subjectRepository.findById(anyLong())).thenReturn(java.util.Optional.of(subject));

        subjectService.updateById(1L, subjectUpdateDto, loggedInUser);

        verify(subjectRepository, times(1)).findById(anyLong());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void deleteById() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);
        loggedInUser.setMainDirectoryPath("path");

        Subject subject = new Subject();
        subject.setTeacherId(1L);
        subject.setFolderName("folder");

        when(subjectRepository.findById(anyLong())).thenReturn(java.util.Optional.of(subject));

        subjectService.deleteById(1L, loggedInUser);

        verify(subjectRepository, times(1)).findById(anyLong());
        verify(enrollmentRepository, times(1)).deleteAllBySubjectId(anyLong());
        verify(subjectRepository, times(1)).deleteById(anyLong());
    }
}