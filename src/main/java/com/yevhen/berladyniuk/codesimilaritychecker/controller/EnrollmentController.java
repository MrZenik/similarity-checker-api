package com.yevhen.berladyniuk.codesimilaritychecker.controller;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.EnrollmentRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/{userId}")
    public List<Subject> getStudentsSubjects(@PathVariable Long userId, @AuthenticationPrincipal UserDto userDto) {
        return this.enrollmentService.getAllStudentSubjectsById(userId, userDto);
    }

    @GetMapping("/get-users/{subjectId}")
    public List<UserDto> getAllStudentsBySubjectId(@PathVariable Long subjectId, @AuthenticationPrincipal UserDto userDto) {
        return this.enrollmentService.getAllStudentsBySubjectId(subjectId, userDto);
    }

    @PostMapping("/enroll")
    public void enrollStudent(@RequestBody EnrollmentRequest enrollmentRequest,
                              @AuthenticationPrincipal UserDto userDto
    ) {
        this.enrollmentService.enroll(enrollmentRequest, userDto);
    }

}
