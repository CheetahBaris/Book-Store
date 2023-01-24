package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.ContactConfirmationPayload;
import com.example.MyBookShopApp.data.dto.ContactConfirmationResponse;
import com.example.MyBookShopApp.data.dto.RegistrationForm;
import com.example.MyBookShopApp.data.user.UserEntity;
import com.example.MyBookShopApp.errs.InvalidJwtTokenException;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;
    private final UserRepository userRepository;

//    @MockBean
//    private UserRepository bookstoreUserRepositoryMock;
    @MockBean
    AuthenticationManager authenticationManagerMock;

    @Autowired
    public BookstoreUserRegisterTests(BookstoreUserRegister userRegister, UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        this.userRegister = userRegister;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@mail.org");
        registrationForm.setName("Tester");
        registrationForm.setPass("iddqd");
        registrationForm.setPhone("9031232323");
    }

    @AfterEach
    void tearDown() {
       UserEntity tester = userRepository.findUserEntitiesByEmail("test@mail.org");
       userRepository.delete(tester);

        registrationForm = null;
    }

    @Test
    void registerNewUser() {
      UserEntity user =  userRegister.registerNewUser(registrationForm);

      assertNotNull(userRepository.findUserEntitiesByEmail(user.getEmail()));

    }

    @Test
    void loginTest(){
        UserEntity user = userRegister.registerNewUser(registrationForm);
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact(user.getEmail());
        payload.setCode(user.getPassword());
        assertSame("true", userRegister.login(payload).getResult());
    }
    @Test
    void loginJWTTest()   throws InvalidJwtTokenException {
        UserEntity user = userRegister.registerNewUser(registrationForm);
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact(user.getEmail());
        payload.setCode(user.getPassword());
        assertEquals(2, userRegister.jwtLogin(payload).getResult().replaceAll("[^.]", "").trim().length());

     }


}