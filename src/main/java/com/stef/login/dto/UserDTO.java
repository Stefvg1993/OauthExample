package com.stef.login.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserDTO {
    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6)
    private String password;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;


    @NotEmpty
    private String email;

    private List<RoleDTO> roles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean active;
}
