package com.stef.login.service;

import com.stef.login.domain.User;
import com.stef.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Find the user that belongs to the username
     *
     * @param username the username to find the user for
     * @return the user belonging to the username
     */
    public User findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Indicates whether the user with the given username is present in the app.
     *
     * @param id the id to check
     * @return true if the user exists, false otherwise
     */
    public boolean exists(final Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Adds a user to the app.
     *
     * @param user the user to add
     */
    public User saveUser(final User user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a user from the app.
     *
     * @param id the id of the user to delete
     */
    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
