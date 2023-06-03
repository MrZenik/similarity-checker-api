package com.yevhen.berladyniuk.codesimilaritychecker.service;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.CheckRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;

public interface PlagiarismCheckService {

    void checkPlagiarism(CheckRequest checkRequest, UserDto loggedInUser);

}
