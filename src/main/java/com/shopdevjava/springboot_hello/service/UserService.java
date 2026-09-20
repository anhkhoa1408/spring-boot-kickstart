package com.shopdevjava.springboot_hello.service;

import com.shopdevjava.springboot_hello.entities.user.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity createUser(UserEntity userEntity);
    UserEntity findByUserNameAndUserEmail(String userName, String userEmail);
    List<UserEntity> getAllUsers();
}
