package com.ensolvers.notes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for user login and registration requests.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AuthRequest {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
