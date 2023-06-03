package com.yevhen.berladyniuk.codesimilaritychecker.services;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.impl.FilesServiceImpl;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FilesServiceImplTest {

    @Mock
    DirectoryManager directoryManager;

    @Mock
    UserRepository userRepository;

    @Mock
    EnrollmentRepository enrollmentRepository;

    @InjectMocks
    FilesServiceImpl filesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getFullTeacherDirectoryStructure() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setMainDirectoryPath("path");

        filesService.getFullTeacherDirectoryStructure(loggedInUser);

        verify(directoryManager, times(1)).getDirectoryStructure(anyString());
    }

    @Test
    void getPartialTeacherDirectoryStructure() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setMainDirectoryPath("path");

        filesService.getPartialTeacherDirectoryStructure("subpath", loggedInUser);

        verify(directoryManager, times(1)).getDirectoryStructure(anyString());
    }

    @Test
    void getDirectoryStructure() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setId(1L);

        when(enrollmentRepository.existsByStudentIdAndSubjectId(anyLong(), anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));

        filesService.getDirectoryStructure("path", 1L, 1L, loggedInUser);

        verify(enrollmentRepository, times(1)).existsByStudentIdAndSubjectId(anyLong(), anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(directoryManager, times(1)).getDirectoryStructure(anyString());
    }

    @Test
    void uploadFiles() {
        UserDto loggedInUser = new UserDto();
        loggedInUser.setRoles(Collections.singletonList(new Role(2L, "ROLE_TEACHER")));
        loggedInUser.setMainDirectoryPath("path");

        MultipartFile[] files = {new MockMultipartFile("file", "Hello, World!".getBytes())};

        filesService.uploadFiles(files, "subpath", 1L, loggedInUser);

        verify(directoryManager, times(1)).uploadFiles(any(MultipartFile[].class), anyString());
    }

    // More tests should be added here for other methods and scenarios.
}
