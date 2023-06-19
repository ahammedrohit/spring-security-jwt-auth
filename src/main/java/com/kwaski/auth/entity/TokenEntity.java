package com.kwaski.auth.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenEntity {
    private String user_name;
    private String accessToken;
    private String refreshToken;
}
