package com.yevhen.berladyniuk.codesimilaritychecker.service.impl;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.exception.ApiException;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.FilesService;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@Service
public class FilesServiceImpl implements FilesService {

    @Value("${code-similarity.main-directory}")
    private String mainDirectory;

    private final DirectoryManager directoryManager;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public FilesServiceImpl(DirectoryManager directoryManager,
                            UserRepository userRepository,
                            EnrollmentRepository enrollmentRepository
    ) {
        this.directoryManager = directoryManager;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Map<String, Object> getFullTeacherDirectoryStructure(UserDto loggedInUser) {
        return this.directoryManager.getDirectoryStructure(loggedInUser.getMainDirectoryPath());
    }

    @Override
    public Map<String, Object> getPartialTeacherDirectoryStructure(String path, UserDto loggedInUser) {
        if (Objects.nonNull(path) && !path.isBlank()) {
            return this.directoryManager.getDirectoryStructure(loggedInUser.getMainDirectoryPath() + "/" + path);
        }
        return this.directoryManager.getDirectoryStructure(loggedInUser.getMainDirectoryPath());
    }

    @Override
    public Map<String, Object> getDirectoryStructure(String path, Long teacherId, Long subjectId, UserDto loggedInUser) {
        if (this.enrollmentRepository.existsByStudentIdAndSubjectId(loggedInUser.getId(), subjectId)) {
            User teacher = this.userRepository.findById(teacherId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher does not exist!"));
            return directoryManager.getDirectoryStructure(teacher.getMainDirectoryPath() + "/" + path + "/" + loggedInUser.getEmail());
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
    }

    @Override
    public void uploadFiles(MultipartFile[] files, String path, Long teacherId, UserDto loggedInUser) {
        List<String> userRoles = loggedInUser.getRoles().stream().map(Role::getName).toList();

        if (userRoles.contains("ROLE_TEACHER")) {
            directoryManager.uploadFiles(files, loggedInUser.getMainDirectoryPath() + "/" + path);
        } else if (userRoles.contains("ROLE_STUDENT")) {
            User teacher = this.userRepository.findById(teacherId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher doesn't exists by id!"));
            directoryManager.uploadFiles(files, teacher.getMainDirectoryPath() + "/" + path + "/" + loggedInUser.getEmail());
        }
    }

    @Override
    public Resource downloadDirectory(String path, UserDto loggedInUser) throws IOException {
        String fullPath = mainDirectory + "/" + loggedInUser.getMainDirectoryPath() + "/" + path;
        File directory = new File(fullPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException("Directory not found");
        }

        String zipFileName = directory.getName() + ".zip";
        File zipFile = new File(directory.getParent(), zipFileName);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipDirectory(directory, directory.getName(), zipOutputStream);
        }

        Resource resource = new FileSystemResource(zipFile);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Archive file not found or is not readable");
        }
    }

    @Override
    public Resource downloadFile(String path, UserDto loggedInUser) throws IOException {
        String fullPath = mainDirectory + "/" + loggedInUser.getMainDirectoryPath() + "/" + path;
        File file = new File(fullPath);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found");
        }

        String fileName = file.getName() + ".zip";
        File zipFile = new File(file.getParent(), fileName);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            FileInputStream fileInputStream = new FileInputStream(file);
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, length);
            }
            fileInputStream.close();
        }

        Resource resource = new FileSystemResource(zipFile);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Archive file not found or is not readable");
        }
    }

    @Override
    public void deleteByPath(String path, boolean isDirectory, UserDto loggedInUser) {
        String fullPath;
        if (path.equals(".zip")) {
            fullPath = loggedInUser.getMainDirectoryPath() + path;
        } else {
            fullPath = loggedInUser.getMainDirectoryPath() + "/" + path;
        }

        if (isDirectory) {
            this.directoryManager.deleteDirectory(fullPath);
        } else {
            this.directoryManager.deleteFile(fullPath);
        }

    }

    private void zipDirectory(File directory, String parentPath, ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            byte[] buffer = new byte[1024];
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectory(file, parentPath + "/" + file.getName(), zipOutputStream);
                } else {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    zipOutputStream.putNextEntry(new ZipEntry(parentPath + "/" + file.getName()));
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    fileInputStream.close();
                }
            }
        }
    }

}


