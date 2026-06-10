package com.example.project.dto.request;

import com.example.project.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Email(message = "Email invalid format")
    private String email;

    private Boolean enabled;

    private Role role;
}