package com.yevhen.berladyniuk.codesimilaritychecker.controller;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/approve/{userId}")
    public void approveTeacherRequest(@PathVariable Long userId) {
        this.adminService.approveTeacherStatusByUserId(userId);
    }

    @PostMapping("/set-student/{userId}")
    public void setStudentRole(@PathVariable Long userId) {
        this.adminService.setStudentRoleByUserId(userId);
    }

    @GetMapping("/get-not-approved")
    public List<UserDto> getWaitingForApprove() {
        return this.adminService.getWaitingForApprove();
    }

    @DeleteMapping("/{id}")
    public boolean deleteUserAccount(@PathVariable Long id) {
        return adminService.deleteUserAccountById(id);
    }

}
