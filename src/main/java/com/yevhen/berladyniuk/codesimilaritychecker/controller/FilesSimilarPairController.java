package com.yevhen.berladyniuk.codesimilaritychecker.controller;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.FilesSimilarPairDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.StudentStatistic;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.FilesSimilarPair;
import com.yevhen.berladyniuk.codesimilaritychecker.service.FilesSimilarPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/files-similar-pairs")
public class FilesSimilarPairController {

    private final FilesSimilarPairService filesSimilarPairService;

    @Autowired
    public FilesSimilarPairController(FilesSimilarPairService filesSimilarPairService) {
        this.filesSimilarPairService = filesSimilarPairService;
    }

    @GetMapping
    public List<StudentStatistic> getStudentStatistic(@RequestParam String email,
                                                      @RequestParam Long subjectId,
                                                      @AuthenticationPrincipal UserDto loggedInUser
    ) {
        return this.filesSimilarPairService.getStudentStatistic(email, subjectId, loggedInUser);
    }

    @GetMapping("/{id}")
    public FilesSimilarPair getById(@PathVariable Long id, @AuthenticationPrincipal UserDto loggedInUser) {
        return this.filesSimilarPairService.getByIdAndTeacher(id, loggedInUser);
    }
}
