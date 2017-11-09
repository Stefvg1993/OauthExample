package com.stef.login.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserConfirmationDTO {

    @NotEmpty
    private String username;

    @NotEmpty
    private String confirmationCode;
}
