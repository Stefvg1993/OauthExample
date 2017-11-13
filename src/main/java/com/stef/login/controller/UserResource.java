package com.stef.login.controller;

import com.stef.login.domain.User;
import com.stef.login.dto.ResetPasswordDTO;
import com.stef.login.dto.UserConfirmationDTO;
import com.stef.login.dto.UserDTO;
import com.stef.login.service.RoleService;
import com.stef.login.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/secured/user")
public class UserResource {
    private static final String STANDARD_ROLE_NAME = "STANDARD_USER";

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JavaMailSender sender;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getCurrentUser() {
        final User user = getAuthenticatedUserFromDB();
        if (user == null) {
            ResponseEntity.badRequest().body("User not found");
        }
        return ResponseEntity.ok(toDTO(user));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(userService.findAll().stream().map(this::toDTO).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/unsecured/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@Validated @RequestBody final UserDTO userDTO) throws Exception {
        if (userDTO == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        final User user = fromDTO(userDTO);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User already exists");
        }

        user.setRoles(Collections.singletonList(roleService.findByRoleName(STANDARD_ROLE_NAME)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(false);
        user.setConfirmationCode(UUID.randomUUID().toString());
        userService.saveUser(user);
        sendConfirmationEmail(userDTO.getEmail(), user.getUsername(), user.getConfirmationCode());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('STANDARD_USER')")
    public ResponseEntity updateUser(@Validated @RequestBody final UserDTO userDTO) {
        if (userDTO == null || userDTO.getId() == null) {
            return ResponseEntity.badRequest().body("You can only update a user with the same id as yourself");
        }
        final User user = fromDTO(userDTO);
        final User authenticatedUser = getAuthenticatedUserFromDB();
        if (isSameUserOrAdministrator(user, authenticatedUser)) {
            return ResponseEntity.badRequest().body("You can only update a user with the same id as yourself");
        }
        user.setRoles(authenticatedUser.getRoles());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmationCode(null);
        userService.saveUser(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity deleteUser(@PathVariable final Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Id cannot be null");
        }
        if (!userService.exists(id)) {
            return ResponseEntity.badRequest().body("User doesn't exist!");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/unsecured/confirm", method = RequestMethod.POST)
    public ResponseEntity confirmUser(@Validated @RequestBody final UserConfirmationDTO userConfirmationDTO) {
        final User user = userService.findByUsername(userConfirmationDTO.getUsername());
        if (user != null && user.isActive()) {
            return ResponseEntity.ok().build();
        }
        if (user == null || !userConfirmationDTO.getConfirmationCode().equals(user.getConfirmationCode())) {
            return ResponseEntity.badRequest().body("Confirmation code doesn't match the users confirmation code");
        }
        user.setActive(true);
        user.setConfirmationCode(null);
        userService.saveUser(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/unsecured/requestResetPassword", method = RequestMethod.POST)
    public ResponseEntity requestResetPassword(@RequestBody final String username) throws Exception {
        final User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User with username " + username + " doesn't exist.");
        }
        user.setConfirmationCode(UUID.randomUUID().toString());
        userService.saveUser(user);
        sendResetPasswordEmail(user.getEmail(), user.getUsername(), user.getConfirmationCode());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/unsecured/resetPassword", method = RequestMethod.POST)
    public ResponseEntity requestResetPassword(@Validated @RequestBody final ResetPasswordDTO resetPasswordDTO) {
        final User user = userService.findByUsername(resetPasswordDTO.getUsername());
        if (user == null) {
            return ResponseEntity.badRequest().body("User with username " + resetPasswordDTO.getUsername() + " doesn't exist.");
        }
        if (!resetPasswordDTO.getConfirmationCode().equals(user.getConfirmationCode())) {
            return ResponseEntity.badRequest().body("Reset password code doesn't exist.");
        }
        if (user.isActive()) {
            user.setConfirmationCode(null);
        }
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userService.saveUser(user);
        return ResponseEntity.ok().build();
    }


    private UserDTO toDTO(final User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User fromDTO(final UserDTO userDTO) {
        final User user = userService.findByUsername(userDTO.getUsername());
        if (user != null) {
            final Long id = user.getId();
            modelMapper.map(userDTO, user);
            user.setId(id);
            return user;
        }
        return modelMapper.map(userDTO, User.class);
    }

    private boolean isSameUserOrAdministrator(@RequestBody final User user, final User authenticatedUser) {
        return (authenticatedUser == null || !authenticatedUser.getId().equals(user.getId())) && !authenticatedUser.isAdministrator();
    }

    private User getAuthenticatedUserFromDB() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String authenticatedUserName = (String) authentication.getPrincipal();
        return userService.findByUsername(authenticatedUserName);
    }


    private void sendConfirmationEmail(final String email, final String username, final String confirmationCode) throws Exception {
        final MimeMessage message = sender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setText("Confirmation link: \nURL: http://localhost:4200/confirmUser?username=" + username + "&code=" + confirmationCode);
        helper.setSubject("Confirm user");
        sender.send(message);
    }

    private void sendResetPasswordEmail(final String email, final String username, final String confirmationCode) throws Exception {
        final MimeMessage message = sender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setText("Reset password link: \nURL: http://localhost:4200/resetPassword?username=" + username + "&code=" + confirmationCode);
        helper.setSubject("Reset password");
        sender.send(message);
    }
}
