package com.yevhen.berladyniuk.codesimilaritychecker.service.impl;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.EnrollmentRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.exception.ApiException;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Enrollment;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.SubjectRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.UserRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.EnrollmentService;
import com.yevhen.berladyniuk.codesimilaritychecker.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserMapper userMapper;

    @Autowired
    public EnrollmentServiceImpl(UserRepository userRepository,
                                 SubjectRepository subjectRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 UserMapper userMapper) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void enroll(EnrollmentRequest enrollmentRequest, UserDto loggedInUser) {
        Subject subject = subjectRepository.findById(enrollmentRequest.getSubjectId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Subject not found"));

        if (subject.getTeacherId().equals(loggedInUser.getId())) {
            List<Enrollment> enrollments = new ArrayList<>();
            Long subjectId = enrollmentRequest.getSubjectId();

            for (Long id : enrollmentRequest.getStudentIds()) {
                if (this.userRepository.existsById(id)) {
                    if (this.enrollmentRepository.existsByStudentIdAndSubjectId(id, subjectId)) {
                        Enrollment enrollment = this.enrollmentRepository.findByStudentIdAndSubjectId(id, subjectId).get();
                        this.enrollmentRepository.deleteById(enrollment.getId());
                    } else {
                        Enrollment enrollment = Enrollment.builder()
                                .student(userRepository.findById(id).get())
                                .subject(subject)
                                .build();
                        enrollments.add(enrollment);
                    }
                }

            }
            this.enrollmentRepository.saveAll(enrollments);
            return;
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "Access denied!");
    }

    @Override
    public List<Subject> getAllStudentSubjectsById(Long id, UserDto loggedInUser) {
        if (loggedInUser.getId().equals(id)) {
            return this.enrollmentRepository.findAllSubjectsByUserId(id);
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "Have to access to another student subjects");
    }

    @Override
    public List<UserDto> getAllStudentsBySubjectId(Long id, UserDto loggedInUser) {
        if (this.subjectRepository.existsByIdAndTeacherId(id, loggedInUser.getId())) {
            return this.enrollmentRepository.findUsersBySubjectId(id)
                    .stream()
                    .map(userMapper::fromUserToDto)
                    .toList();
        }
        throw new ApiException(HttpStatus.NOT_FOUND, "Subject not found by sd and teacherId");
    }

}
