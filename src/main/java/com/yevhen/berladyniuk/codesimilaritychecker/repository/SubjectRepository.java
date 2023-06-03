package com.yevhen.berladyniuk.codesimilaritychecker.repository;

import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Long> {

    List<Subject> findAllByTeacherId(Long id);

    boolean existsByIdAndTeacherId(Long id, Long teacherId);

}
