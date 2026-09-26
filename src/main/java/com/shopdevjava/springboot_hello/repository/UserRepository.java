package com.shopdevjava.springboot_hello.repository;

import com.shopdevjava.springboot_hello.entities.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
//import org.springframework.stereotype.Repository;
//import org.springframework.data.repository.RepositoryDefinition;

//@RepositoryDefinition(domainClass = UserEntity.class, idClass = Long.class)
//@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    Page<UserEntity> findByUserName(String userName, Pageable pageable);

    UserEntity findByUserEmailEndingWith(String userEmail);

    UserEntity findByUserNameAndUserEmail(String userName, String userEmail);

    @Query("SELECT u FROM UserEntity u WHERE u.Id = (SELECT MAX(p.Id) from UserEntity p)")
    UserEntity findMaxId();

    @Query("SELECT u FROM UserEntity u WHERE u.userName = ?1 AND u.userEmail = ?2")
    List<UserEntity> findByUserByEntity(String userName, String userEmail);

    @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName AND u.userEmail = :userEmail")
    List<UserEntity> findByUserByEntityV2(@Param("userName") String userName, @Param("userEmail") String userEmail);

    @Modifying
    @Query("SELECT u FROM UserEntity u WHERE u.userEmail = :userEmail")
    @Transactional
    int updateUserByEmail(@Param("userEmail") String userEmail);
}
