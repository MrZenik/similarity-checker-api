package com.yevhen.berladyniuk.codesimilaritychecker.service;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;

import java.util.List;

public interface AdminService {

    void approveTeacherStatusByUserId(Long userId);

    void setStudentRoleByUserId(Long userId);

    List<UserDto> getWaitingForApprove();

    boolean deleteUserAccountById(Long id);

}
