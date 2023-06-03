package com.yevhen.berladyniuk.codesimilaritychecker.service.impl;

import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectCreateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.SubjectUpdateDto;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.exception.ApiException;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.EnrollmentRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.repository.SubjectRepository;
import com.yevhen.berladyniuk.codesimilaritychecker.service.SubjectService;
import com.yevhen.berladyniuk.codesimilaritychecker.util.DirectoryManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DirectoryManager directoryManager;

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository,
                              EnrollmentRepository enrollmentRepository,
                              DirectoryManager directoryManager
    ) {
        this.subjectRepository = subjectRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.directoryManager = directoryManager;
    }

    @Override
    public Subject create(SubjectCreateDto subjectCreateDto, UserDto userDto) {
        Subject subject = Subject.builder()
                .name(subjectCreateDto.getName())
                .folderName(subjectCreateDto.getFolderName())
                .teacherId(userDto.getId())
                .build();
        return subjectRepository.save(subject);
    }

    @Override
    public Subject getById(Long id, UserDto loggedInUser) {
        if (this.checkIfExistByIdAndTeacherIdOrThrow(id, loggedInUser.getId())) {
            return this.getByIdOrElseThrow(id);
        }
        return null;
    }

    @Override
    public List<Subject> getAllByTeacher(Long teacherId) {
        return this.subjectRepository.findAllByTeacherId(teacherId);
    }

    @Override
    public Subject updateById(Long id, SubjectUpdateDto subjectUpdateDto, UserDto loggedInUser) {
        Subject subject = this.getByIdOrElseThrow(id);
        if (!subject.getTeacherId().equals(loggedInUser.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not rights to update not your subject");
        }
        subject.setName(subjectUpdateDto.getName());

        return this.subjectRepository.save(subject);
    }

    @Override
    public void deleteById(Long id, UserDto loggedInUser) {
        Subject subject = this.getByIdOrElseThrow(id);
        if (!subject.getTeacherId().equals(loggedInUser.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not rights to delete not your subject");
        }
//        this.directoryManager.deleteDirectory(loggedInUser.getMainDirectoryPath() + "/" + subject.getFolderName());
        this.enrollmentRepository.deleteAllBySubjectId(id);
        this.subjectRepository.deleteById(id);
    }

    private Subject getByIdOrElseThrow(Long id) {
        return this.subjectRepository.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "Subject doesn't exist!")
        );
    }

    private boolean checkIfExistByIdAndTeacherIdOrThrow(Long id, Long teacherId) {
        if (this.subjectRepository.existsByIdAndTeacherId(id, teacherId)) {
            return true;
        }
        throw new ApiException(HttpStatus.NOT_FOUND, "Subject doesn't exist!");
    }

}
