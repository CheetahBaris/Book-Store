package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public BookstoreUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserEntity bookstoreUser = null;

        if (!s.contains("@")) {

            bookstoreUser = userRepository.findUserEntitiesByPhone(s);
        } else {

            bookstoreUser = userRepository.findUserEntitiesByEmail(s);
        }
        if (bookstoreUser != null) {

            return new BookstoreUserDetails(bookstoreUser);
        } else {

            throw new UsernameNotFoundException("user not found!");
        }
    }

    public UserDetails getUserByEmail(String email) {
        UserEntity user = userRepository.findUserEntitiesByEmail(email);

        if (user != null) {

            return new BookstoreUserDetails(user);
        } else {

            return null;
        }
    }

    public UserDetails getUserByPhone(String phone) {
        UserEntity user = userRepository.findUserEntitiesByPhone(phone);
        if (user != null) {
            return new BookstoreUserDetails(user);
        } else {
            return null;
        }
    }
}
