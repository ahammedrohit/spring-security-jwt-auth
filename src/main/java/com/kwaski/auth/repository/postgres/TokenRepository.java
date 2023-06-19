package com.kwaski.auth.repository.postgres;

import com.kwaski.auth.entity.TokenEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenRepository {
    void saveToken(TokenEntity token);

    String findAccessTokenByUserName(String user_name);
}
