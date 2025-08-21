package com.wizwrite.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String username;
}
