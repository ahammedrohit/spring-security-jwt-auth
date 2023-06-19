package com.kwaski.auth.service;

import com.kwaski.auth.entity.TokenEntity;
import com.kwaski.auth.repository.postgres.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveToken(TokenEntity token) {
        tokenRepository.saveToken(token);
    }

    public String findAccessTokenByUserName(String userName) {
        return tokenRepository.findAccessTokenByUserName(userName);
    }

}
