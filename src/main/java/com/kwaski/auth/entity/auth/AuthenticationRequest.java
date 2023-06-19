package com.kwaski.auth.entity.auth;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String user_name;
    private String password;
}
