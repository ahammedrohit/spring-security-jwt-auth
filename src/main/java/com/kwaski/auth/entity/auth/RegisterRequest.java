package com.kwaski.auth.entity.auth;

import lombok.Data;

@Data
public class RegisterRequest {

    private String user_name;
    private String email;
    private String password;
}
