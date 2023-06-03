package com.yevhen.berladyniuk.codesimilaritychecker.repository;

import com.yevhen.berladyniuk.codesimilaritychecker.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAllByEmailContainsAndRoles_Name(String email, String roleName);

    boolean existsByEmail(String email);

    List<User> getAllByIsApprovedIsFalse();

}
