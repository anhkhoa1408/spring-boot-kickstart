package com.shopdevjava.springboot_hello.service;

import com.shopdevjava.springboot_hello.entities.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {
    UserEntity createUser(UserEntity userEntity);
    UserEntity findByUserNameAndUserEmail(String userName, String userEmail);
    List<UserEntity> getAllUsers();

    Page<UserEntity> findAllUsers(Pageable pageable);

    Page<UserEntity> findByUserName(String userName, Pageable pageable);
}
