package com.yevhen.berladyniuk.codesimilaritychecker.repository;

import com.yevhen.berladyniuk.codesimilaritychecker.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> getRoleByName(String name);

}
