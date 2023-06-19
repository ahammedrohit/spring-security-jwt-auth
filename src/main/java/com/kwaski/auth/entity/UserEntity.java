package com.kwaski.auth.entity;

import lombok.Data;

@Data
public class UserEntity {

    private String user_name;
    private String email;
    private String password;
}
