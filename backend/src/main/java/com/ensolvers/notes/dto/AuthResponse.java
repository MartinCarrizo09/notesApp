package com.ensolvers.notes.dto;

import lombok.*;

/**
 * DTO for returning authentication responses (JWT token and message).
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String message;
}
