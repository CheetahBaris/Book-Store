package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.user.UserEntity;
import com.example.MyBookShopApp.errs.InvalidJwtTokenException;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import com.example.MyBookShopApp.data.dto.ContactConfirmationPayload;
import com.example.MyBookShopApp.data.dto.ContactConfirmationResponse;
import com.example.MyBookShopApp.data.dto.RegistrationForm;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class BookstoreUserRegister {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;

    @Autowired
    public BookstoreUserRegister(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 BookstoreUserDetailsService bookstoreUserDetailsService,
                                 JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public UserEntity registerNewUser(RegistrationForm registrationForm) {

        if (userRepository.findUserEntitiesByEmail(registrationForm.getEmail()) == null) {
            UserEntity user = new UserEntity();
            user.setName(registrationForm.getName());
            user.setEmail(registrationForm.getEmail());
            user.setPhone(registrationForm.getPhone());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            user.setHash(passwordEncoder.encode(String.valueOf(new Random().nextInt(10000))));
            user.setRegTime(LocalDateTime.now());
            user.setBalance(0);

            userRepository.save(user);
            return user;
        } else {
            return null;
        }
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                        payload.getCode()));


        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    //TODO: если повториться ошибка аутентификации с просроченным токеном , посмотреть здесь!
    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) throws InvalidJwtTokenException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        BookstoreUserDetails userDetails = null;
        try {

            userDetails =
                    (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        } catch (Exception e) {
            throw new InvalidJwtTokenException("token invalid!");
        }

        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    //TODO: если повториться ошибка аутентификации с просроченным токеном , посмотреть здесь!

    public UserEntity getCurrentUser() throws InvalidJwtTokenException {
        if (SecurityContextHolder.getContext() != null) {

            try {

                BookstoreUserDetails userDetails =
                        (BookstoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                return userDetails.getBookstoreUser();
            } catch (Exception e) {
                throw new InvalidJwtTokenException("invalid token!");
            }

        } else {
            return null;
        }
    }
}
