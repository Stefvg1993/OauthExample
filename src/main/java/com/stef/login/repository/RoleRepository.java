package com.stef.login.repository;

import com.stef.login.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByRoleName(String roleName);
}
