package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.ContactConfirmationPayload;
import com.example.MyBookShopApp.data.dto.RegistrationForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class AuthUserControllerTests {

    private final MockMvc mockMvc;
    private RegistrationForm registrationForm;

    @Autowired
    AuthUserControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {

        registrationForm = new RegistrationForm();
        registrationForm.setEmail("noCode@gmail.com");
        registrationForm.setName("NoCode");
        registrationForm.setPass("noCode");
        registrationForm.setPhone("+7 (944) 234-22-44");


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void signInPageAccessTest() throws Exception {
        mockMvc.perform(get("/signin"))
                .andDo(print())
                .andExpect(content().string(containsString("Вход")))
                .andExpect(status().isOk());
    }
    @Test
    public void signUpPageAccessTest() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(content().string(containsString("Регистрация")))
                .andExpect(status().isOk());
    }

    @Test
    void emailLoginTest() throws Exception {
        mockMvc
                .perform(formLogin("/signin").user("terster@gmail.com").password("1234"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
    @Test
    void phoneLoginTest() throws Exception {
        mockMvc
                .perform(formLogin("/signin").user("+7 (925) 333-22-33").password("1234"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
    @Test
    void handleSignUp() throws Exception {
        mockMvc
                 .perform(post("/reg")
                         .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                         .param("name", registrationForm.getName())
                                 .param("phone",registrationForm.getPhone())
                                 .param("phoneCode", "123+123")
                                 .param( "phoneCode", "123+123")
                                 .param("mailCode", "123+123")
                                 .param("email",registrationForm.getEmail())
                                 .param("pass", registrationForm.getPass()))
                .andExpect(model().attribute("regOk",true));
    }
    @Test
    @WithUserDetails("buzzell0@quantcast.com")
    void logoutTest() throws Exception{
        mockMvc
                .perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }

}