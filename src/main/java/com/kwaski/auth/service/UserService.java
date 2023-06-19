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

    public boolean createUser(UserEntity user) {
//        System.out.println(user);
        return userRepository.createUser(user);
    }
}
