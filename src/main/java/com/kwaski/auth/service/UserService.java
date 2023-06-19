package com.kwaski.auth.service;

import com.kwaski.auth.entity.UserEntity;
import com.kwaski.auth.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserEntity> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }


    public boolean createUser(UserEntity user) {
        return userRepository.createUser(user);
    }
}
