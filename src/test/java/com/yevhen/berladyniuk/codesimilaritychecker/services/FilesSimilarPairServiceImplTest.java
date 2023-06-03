package com.yevhen.berladyniuk.codesimilaritychecker.services;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.FilesSimilarPair;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.FilesSimilarPairRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.SubjectRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.impl.FilesSimilarPairServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FilesSimilarPairServiceImplTest {

    @Mock
    FilesSimilarPairRepository filesSimilarPairRepository;

    @Mock
    SubjectRepository subjectRepository;

    @InjectMocks
    FilesSimilarPairServiceImpl filesSimilarPairService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getStudentStatistic() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);
        loggedInUser.setMainDirectoryPath("path");

        Subject subject = new Subject();
        subject.setFolderName("folder");

        FilesSimilarPair filesSimilarPair = FilesSimilarPair.builder()
                .file1("/home/user/main/path/email/file1")
                .file2("/home/user/main/path/email/file2")
                .code1("code1")
                .code2("code2")
                .similarityScore(0.75)
                .build();

        when(subjectRepository.existsByIdAndTeacherId(anyLong(), anyLong())).thenReturn(true);
        when(subjectRepository.findById(anyLong())).thenReturn(java.util.Optional.of(subject));
        when(filesSimilarPairRepository.findAllByFile1StartingWithOrderBySimilarityScoreDesc(anyString())).thenReturn(Collections.singletonList(filesSimilarPair));

        filesSimilarPairService.getStudentStatistic("email", 1L, loggedInUser);

        verify(subjectRepository, times(1)).existsByIdAndTeacherId(anyLong(), anyLong());
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(filesSimilarPairRepository, times(1)).findAllByFile1StartingWithOrderBySimilarityScoreDesc(anyString());
    }

    @Test
    void getByIdAndTeacher() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);
        loggedInUser.setMainDirectoryPath("path");

        FilesSimilarPair filesSimilarPair = new FilesSimilarPair();
        filesSimilarPair.setTeacherId(1L);
        filesSimilarPair.setFile1("path/file1");
        filesSimilarPair.setFile2("path/file2");

        when(filesSimilarPairRepository.findById(anyLong())).thenReturn(java.util.Optional.of(filesSimilarPair));

        filesSimilarPairService.getByIdAndTeacher(1L, loggedInUser);

        verify(filesSimilarPairRepository, times(1)).findById(anyLong());
    }
}
