package com.marcoaga02.modularhub.modules.usermanagement.controller;

import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.service.LanguageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LanguageController.class)
class LanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LanguageService languageService;

    @Test
    void testGetLanguagesShouldReturnListOfLanguages() throws Exception {
        LanguageResponseDTO languageResponseDto = new LanguageResponseDTO();
        languageResponseDto.setId("uuid");
        languageResponseDto.setCode("it-IT");
        languageResponseDto.setLabel("Italiano");
        languageResponseDto.setIsDefault(true);

        when(languageService.getLanguages()).thenReturn(List.of(languageResponseDto));

        mockMvc.perform(get("/languages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("uuid"))
                .andExpect(jsonPath("$[0].code").value("it-IT"))
                .andExpect(jsonPath("$[0].label").value("Italiano"))
                .andExpect(jsonPath("$[0].isDefault").value(true));
    }

}
