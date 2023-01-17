package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRepositoryTests {

    private final UserRepository bookstoreUserRepository;

    @Autowired
    public BookstoreUserRepositoryTests(UserRepository bookstoreUserRepository) {
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Test
    public void testAddNewUser(){
        UserEntity user = new UserEntity();
        user.setPassword("1234567890");
        user.setPhone("9031232323");
        user.setName("Tester");
        user.setEmail("testing@mailtest.org");

        assertNotNull(bookstoreUserRepository.save(user));
    }
}