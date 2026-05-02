package com.marcoaga02.modularhub.shared.controller;

import com.marcoaga02.modularhub.shared.dto.AccountDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;
import com.marcoaga02.modularhub.shared.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.shared.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        LanguageResponseDTO language = new LanguageResponseDTO();
        language.setId("lang-123");
        language.setCode("it-IT");
        language.setLabel("Italiano");
        language.setIsDefault(true);

        AccountPreferencesResponseDTO preferences = new AccountPreferencesResponseDTO();
        preferences.setLanguage(language);

        accountDTO = new AccountDTO(
                "identity-123",
                "mario@example.com",
                "mario.rossi",
                "Mario",
                "Rossi",
                Set.of("ROLE_USER"),
                preferences
        );
    }

    @Test
    void getCurrentAccount_shouldReturnOkWithAccountData() throws Exception {
        when(accountService.getCurrentAccount()).thenReturn(accountDTO);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.identityId").value("identity-123"))
                .andExpect(jsonPath("$.email").value("mario@example.com"))
                .andExpect(jsonPath("$.username").value("mario.rossi"))
                .andExpect(jsonPath("$.firstName").value("Mario"))
                .andExpect(jsonPath("$.lastName").value("Rossi"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.preferences.language.id").value("lang-123"))
                .andExpect(jsonPath("$.preferences.language.code").value("it-IT"))
                .andExpect(jsonPath("$.preferences.language.label").value("Italiano"))
                .andExpect(jsonPath("$.preferences.language.isDefault").value(true));

        verify(accountService, times(1)).getCurrentAccount();
    }

}