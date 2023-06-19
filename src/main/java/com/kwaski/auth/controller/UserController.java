package com.kwaski.auth.controller;

import com.kwaski.auth.entity.UserEntity;
import com.kwaski.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public String getAllUsers() {
        return userService.getAllUsers().toString();
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserEntity user) {
//        System.out.println(user);
        userService.createUser(user);
        return "Hello, " + user.getUser_name();
    }
}
