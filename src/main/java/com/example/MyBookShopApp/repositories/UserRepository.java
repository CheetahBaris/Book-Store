package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findUserEntitiesByName(String name);

    @Query(value = "SELECT MAX(id) FROM users;",nativeQuery = true)
    Long getMaxId();

    UserEntity findUserEntitiesByEmail(String email);
    UserEntity findUserEntitiesByPhone(String phone);
}
