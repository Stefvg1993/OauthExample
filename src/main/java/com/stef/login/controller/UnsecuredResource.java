package com.stef.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;

@RestController
@RequestMapping("/unsecured")
public class UnsecuredResource {

    private final JavaMailSender sender;

    @Autowired
    public UnsecuredResource(final JavaMailSender sender) {
        this.sender = sender;
    }

    @RequestMapping("/mail")
    public ResponseEntity sendMail() {
        try {
            sendEmail();
            return ResponseEntity.ok().build();
        } catch (final Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    private void sendEmail() throws Exception {
        final MimeMessage message = sender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo("stefvg1993@gmail.com");
        helper.setText("How are you?");
        helper.setSubject("Hi");
        sender.send(message);
    }

}
