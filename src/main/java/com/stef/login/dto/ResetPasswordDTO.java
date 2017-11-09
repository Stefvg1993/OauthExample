package com.stef.login.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordDTO {

    @NotEmpty
    private String username;

    @NotEmpty
    private String confirmationCode;

    @Size(min = 6)
    private String newPassword;

}
