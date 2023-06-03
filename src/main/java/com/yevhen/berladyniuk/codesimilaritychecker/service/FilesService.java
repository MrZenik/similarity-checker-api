package com.yevhen.berladyniuk.codesimilaritychecker.service;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FilesService {

    Map<String, Object> getFullTeacherDirectoryStructure(UserDto loggedInUser);

    Map<String, Object> getPartialTeacherDirectoryStructure(String path, UserDto loggedInUser);

    Map<String, Object> getDirectoryStructure(String path, Long teacherId, Long subjectId, UserDto loggedInUser);

    void uploadFiles(MultipartFile[] files, String path, Long teacherId, UserDto loggedInUser);

    Resource downloadDirectory(String path, UserDto loggedInUser) throws IOException;

    Resource downloadFile(String path, UserDto loggedInUser) throws IOException;

    void deleteByPath(String path, boolean isDirectory, UserDto loggedInUser);

}

