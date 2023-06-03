package com.yevhen.berladyniuk.codesimilaritychecker.repository;

import com.yevhen.berladyniuk.codesimilaritychecker.model.Enrollment;
import com.yevhen.berladyniuk.codesimilaritychecker.model.Subject;
import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {

    @Query("SELECT e.subject FROM Enrollment e WHERE e.student.id = :userId")
    List<Subject> findAllSubjectsByUserId(@Param("userId") Long userId);

    @Query("SELECT e.student FROM Enrollment e WHERE e.subject.id = :subjectId")
    List<User> findUsersBySubjectId(@Param("subjectId") Long subjectId);

    Optional<Enrollment> findByStudentIdAndSubjectId(Long userId, Long subjectId);

    boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);

    void deleteAllBySubjectId(Long id);

    void deleteAllByStudentId(Long id);

}

