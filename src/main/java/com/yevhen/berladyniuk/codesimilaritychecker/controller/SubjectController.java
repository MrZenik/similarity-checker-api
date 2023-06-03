package com.yevhen.berladyniuk.codesimilaritychecker.controller;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectUpdateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public Subject create(@RequestBody SubjectCreateDto subjectCreateDto, @AuthenticationPrincipal UserDto userDto) {
        return this.subjectService.create(subjectCreateDto, userDto);
    }

    @GetMapping
    public List<Subject> getAllByTeacher(@AuthenticationPrincipal UserDto userDto) {
        return this.subjectService.getAllByTeacher(userDto.getId());
    }

    @GetMapping("/{id}")
    public Subject getById(@PathVariable Long id, @AuthenticationPrincipal UserDto loggedInUser) {
        return this.subjectService.getById(id, loggedInUser);
    }

    @PutMapping("/{id}")
    public Subject updateById(@RequestBody SubjectUpdateDto subjectUpdateDto, @PathVariable Long id, @AuthenticationPrincipal UserDto loggedInUser) {
        return this.subjectService.updateById(id, subjectUpdateDto, loggedInUser);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, @AuthenticationPrincipal UserDto userDto) {
        this.subjectService.deleteById(id, userDto);
    }

}
