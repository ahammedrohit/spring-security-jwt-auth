package com.kwaski.auth.repository.postgres;

import com.kwaski.auth.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRepository {

    List<UserEntity> getAllUsers();

    boolean createUser(UserEntity user);

    UserEntity getUserByUsername(String user_name);
}
