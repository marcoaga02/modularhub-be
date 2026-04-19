package com.marcoaga02.modularhub.modules.usermanagement.controller;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Gender;
import com.marcoaga02.modularhub.modules.usermanagement.service.UserService;
import com.marcoaga02.modularhub.shared.constant.PaginationHeaders;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponseDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UserResponseDTO();
        dto.setId("user-uuid");
    }

    @Test
    void testGetAllUsersShouldReturnOkWithListAndPaginationHeaders() throws Exception {
        Page<UserResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any(), any())).thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("user-uuid"))
                .andExpect(header().string(PaginationHeaders.TOTAL_COUNT, "1"))
                .andExpect(header().string(PaginationHeaders.TOTAL_PAGES, "1"))
                .andExpect(header().string(PaginationHeaders.CURRENT_PAGE, "0"))
                .andExpect(header().string(PaginationHeaders.PAGE_SIZE, "10"));
    }

    @Test
    void testGetAllUsersWhenEmptyPageShouldReturnOkWithEmptyList() throws Exception {
        when(userService.getAllUsers(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetUserByUuidWhenUserExistsShouldReturnOk() throws Exception {
        when(userService.getUserByUuid("user-uuid")).thenReturn(dto);

        mockMvc.perform(get("/users/user-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-uuid"));
    }

    @Test
    void testGetUserByUuidWhenUserNotFoundShouldReturn404() throws Exception {
        when(userService.getUserByUuid("not-existing"))
                .thenThrow(new NotFoundException("User with uuid 'not-existing' not found"));

        mockMvc.perform(get("/users/not-existing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUserShouldReturnCreated() throws Exception {
        UserRequestDTO request = buildCreateRequestDTO();

        when(userService.createUser(any())).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user-uuid"));
    }

    @Test
    void testCreateUserWhenMissingRequiredFieldsShouldReturn400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserShouldReturnOk() throws Exception {
        UserRequestDTO request = buildUpdateRequestDTO();

        when(userService.updateUser(eq("user-uuid"), any())).thenReturn(dto);

        mockMvc.perform(put("/users/user-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-uuid"));
    }

    @Test
    void testUpdateUserWhenUserNotFoundShouldReturn404() throws Exception {
        UserRequestDTO request = buildUpdateRequestDTO();

        when(userService.updateUser(eq("not-existing"), any()))
                .thenThrow(new NotFoundException("User with uuid 'not-existing' not found"));

        mockMvc.perform(put("/users/not-existing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserWhenMissingRequiredFieldsShouldReturn400() throws Exception {
        mockMvc.perform(put("/users/user-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/user-uuid"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser("user-uuid");
    }

    @Test
    void deleteUserWhenUserNotFoundShouldReturn404() throws Exception {
        doThrow(new NotFoundException("User with uuid 'not-existing' not found"))
                .when(userService).deleteUser("not-existing");

        mockMvc.perform(delete("/users/not-existing"))
                .andExpect(status().isNotFound());
    }

    private UserRequestDTO buildUpdateRequestDTO() {
        UserRequestDTO request = new UserRequestDTO();
        request.setFirstname("new firstname");
        request.setLastname("new lastname");
        request.setGender(Gender.F);
        request.setLanguageId("lang-uuid");
        request.setTaxIdNumber("TAXID01");
        request.setEmail("new@mock.it");
        request.setUsername("new.username");
        request.setEnabled(false);
        return request;
    }

    private UserRequestDTO buildCreateRequestDTO() {
        UserRequestDTO request = new UserRequestDTO();
        request.setFirstname("Mario");
        request.setLastname("Rossi");
        request.setGender(Gender.M);
        request.setLanguageId("lang-uuid");
        request.setTaxIdNumber("TAXID01");
        request.setEmail("mario@mock.it");
        request.setUsername("mario.rossi");
        request.setPassword("password");
        request.setEnabled(true);
        return request;
    }

}
