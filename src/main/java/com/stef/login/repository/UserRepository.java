package com.stef.login.repository;

import com.stef.login.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Override
    List<User> findAll();
}
