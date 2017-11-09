package com.stef.login.service;

import com.stef.login.domain.Role;
import com.stef.login.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Find the role belonging to the roleName.
     *
     * @param roleName the name of the role to find
     * @return the name of the role
     */
    public Role findByRoleName(final String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

}
