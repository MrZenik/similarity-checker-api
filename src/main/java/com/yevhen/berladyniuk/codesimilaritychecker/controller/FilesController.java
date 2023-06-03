package com.yevhen.berladyniuk.codesimilaritychecker.controller;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FilesService filesService;

    @Autowired
    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping("/get-full-teacher-structure")
    public Map<String, Object> getFullTeacherDirectoryStructure(@AuthenticationPrincipal UserDto userDto) {
        return this.filesService.getFullTeacherDirectoryStructure(userDto);
    }

    @GetMapping("/get-partial-teacher-structure")
    public Map<String, Object> getPartialTeacherDirectoryStructure(@RequestParam String path, @AuthenticationPrincipal UserDto userDto) {
        return this.filesService.getPartialTeacherDirectoryStructure(path, userDto);
    }

    @PostMapping("/get-structure")
    public Map<String, Object> getDirectoryStructure(@RequestParam("path") String path,
                                                     @RequestParam("subjectId") Long subjectId,
                                                     @RequestParam("teacherId") Long teacherId,
                                                     @AuthenticationPrincipal UserDto userDto
    ) {
        return this.filesService.getDirectoryStructure(path, teacherId, subjectId, userDto);
    }

    @PostMapping("/upload")
    public void uploadFiles(@RequestParam("files") MultipartFile[] files,
                            @RequestParam("path") String path,
                            @RequestParam("teacherId") Long teacherId,
                            @AuthenticationPrincipal UserDto loggedInUser
    ) {
        this.filesService.uploadFiles(files, path, teacherId, loggedInUser);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String path,
                                                 @RequestParam(value = "isDirectory") boolean isDirectory,
                                                 @AuthenticationPrincipal UserDto loggedInUser
    ) throws IOException {
        Resource file;
        if (isDirectory) {
            file = this.filesService.downloadDirectory(path, loggedInUser);
        } else {
            file = this.filesService.downloadFile(path, loggedInUser);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/delete")
    public void deleteByPath(@RequestParam("path") String path,
                             @RequestParam(value = "isDirectory") boolean isDirectory,
                             @AuthenticationPrincipal UserDto loggedInUser
    ) {
        this.filesService.deleteByPath(path, isDirectory, loggedInUser);
    }

}
