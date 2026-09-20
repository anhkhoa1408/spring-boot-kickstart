package com.shopdevjava.springboot_hello.controller;

import com.shopdevjava.springboot_hello.entities.user.UserEntity;
import com.shopdevjava.springboot_hello.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public UserEntity addUser(@RequestBody UserEntity userEntity) {
        return userService.createUser(userEntity);
    }

    @GetMapping("/search")
    public UserEntity searchUser(@RequestParam String userName, @RequestParam String userEmail) {
        return userService.findByUserNameAndUserEmail(userName, userEmail);
    }
}
