package com.yevhen.berladyniuk.codesimilaritychecker.controller;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.CheckRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.service.PlagiarismCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class PlagiarismCheckController {

    private final PlagiarismCheckService plagiarismCheckService;

    @Autowired
    public PlagiarismCheckController(PlagiarismCheckService plagiarismCheckService) {
        this.plagiarismCheckService = plagiarismCheckService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> checkPlagiarism(@RequestBody CheckRequest checkRequest,
                                                               @AuthenticationPrincipal UserDto loggedInUser) {
        plagiarismCheckService.checkPlagiarism(checkRequest, loggedInUser);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Check request received");
        return ResponseEntity.ok(response);
    }

}
