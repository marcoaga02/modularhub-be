package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.dto.IdentityGroupDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserCreateRequestDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserUpdateRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock
    private IdentityProviderService identityProvider;

    @InjectMocks
    private IdentityService identityService;

    @Test
    void createUser_shouldCreateUser_whenPasswordIsNotBlank() {
        IdentityUserCreateRequestDTO user = createIdentityUserCreateRequestDto(
                "mrossi",
                "rossi@email.it",
                "Mario",
                "Rossi",
                true,
                true,
                "Password123!",
                List.of("group1", "group2")
        );

        when(identityProvider.createUser(any()))
                .thenReturn("user-id");

        String userId = identityService.createUser(user);

        assertThat(userId).isEqualTo("user-id");
        verify(identityProvider).createUser(user);
        verify(identityProvider).resetPassword("user-id", "Password123!");
    }

    @Test
    void createUser_shouldCreateUser_whenPasswordIsBlank() {
        IdentityUserCreateRequestDTO user = createIdentityUserCreateRequestDto(
                "mrossi",
                "rossi@email.it",
                "Mario",
                "Rossi",
                true,
                true,
                "",
                List.of("group1", "group2")
        );

        when(identityProvider.createUser(any()))
                .thenReturn("user-id");

        String userId = identityService.createUser(user);

        assertThat(userId).isEqualTo("user-id");
        verify(identityProvider).createUser(user);
        verify(identityProvider, never()).resetPassword(any(), anyString());
    }

    @Test
    void createUser_shouldCreateUser_whenPasswordIsNull() {
        IdentityUserCreateRequestDTO user = createIdentityUserCreateRequestDto(
                "mrossi",
                "rossi@email.it",
                "Mario",
                "Rossi",
                true,
                true,
                null,
                List.of("group1", "group2")
        );

        when(identityProvider.createUser(any()))
                .thenReturn("user-id");

        String userId = identityService.createUser(user);

        assertThat(userId).isEqualTo("user-id");
        verify(identityProvider).createUser(user);
        verify(identityProvider, never()).resetPassword(any(), anyString());
    }

    @Test
    void updateUser_shoudUpdateUser() {
        IdentityUserUpdateRequestDTO user = createIdentityUserUpdateRequestDto(
                    "mrossi",
                    "rossi@email.it",
                    "Mario",
                    "Rossi",
                    true,
                    List.of("group1", "group2")
        );

        final String userId = "user-id";

        identityService.updateUser(userId, user);

        verify(identityProvider).updateUser(userId, user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        final String identityId = "identity-id";
        IdentityGroupDTO groupDTO1 = createIdentityGroupDTO("group-id1", "first group");
        IdentityGroupDTO groupDTO2 = createIdentityGroupDTO("group-id2", "second group");

        IdentityUserResponseDTO userResponseDTO = createIdentityUserResponseDto(
                identityId,
                "mrossi",
                "rossi@email.it",
                "Mario",
                "Rossi",
                true,
                List.of(groupDTO1, groupDTO2)
        );

        when(identityProvider.getUserById(identityId)).thenReturn(userResponseDTO);

        IdentityUserResponseDTO result = identityService.getUserById(identityId);

        assertThat(result).isEqualTo(userResponseDTO);
        verify(identityProvider).getUserById(identityId);
    }

    @Test
    void testUpdateUserPassword_shouldUpdateUserPassword_whenPasswordIsNotBlank() {
        final String userId = "user-id";
        final String password = "Password123!";

        identityService.updateUserPassword(userId, password);

        verify(identityProvider).resetPassword(userId, password);
    }

    @Test
    void updateUserPassword_shouldUpdateUserPassword_whenPasswordIsBlank() {
        final String userId = "user-id";
        final String password = "";

        identityService.updateUserPassword(userId, password);

        verify(identityProvider, never()).resetPassword(any(), anyString());
    }

    @Test
    void updateUserPassword_shouldUpdateUserPassword_whenPasswordIsNull() {
        final String userId = "user-id";
        final String password = null;

        identityService.updateUserPassword(userId, password);

        verify(identityProvider, never()).resetPassword(any(), anyString());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        final String userId = "user-id";

        identityService.deleteUser(userId);

        verify(identityProvider).deleteUser(userId);
    }

    private IdentityUserCreateRequestDTO createIdentityUserCreateRequestDto(
            String username,
            String email,
            String firstName,
            String lastName,
            Boolean enabled,
            Boolean emailVerified,
            String password,
            List<String> groupIds
    ) {
        IdentityUserCreateRequestDTO userRequestDTO = new IdentityUserCreateRequestDTO();
        userRequestDTO.setUsername(username);
        userRequestDTO.setEmail(email);
        userRequestDTO.setFirstName(firstName);
        userRequestDTO.setLastName(lastName);
        userRequestDTO.setEnabled(enabled);
        userRequestDTO.setEmailVerified(emailVerified);
        userRequestDTO.setPassword(password);
        userRequestDTO.setGroupIds(groupIds);

        return userRequestDTO;
    }

    private IdentityUserUpdateRequestDTO createIdentityUserUpdateRequestDto(
            String username,
            String email,
            String firstName,
            String lastName,
            Boolean enabled,
            List<String> groupIds
    ) {
        IdentityUserUpdateRequestDTO userUpdateDTO = new IdentityUserUpdateRequestDTO();
        userUpdateDTO.setUsername(username);
        userUpdateDTO.setEmail(email);
        userUpdateDTO.setFirstName(firstName);
        userUpdateDTO.setLastName(lastName);
        userUpdateDTO.setEnabled(enabled);
        userUpdateDTO.setGroupIds(groupIds);

        return userUpdateDTO;
    }

    private IdentityUserResponseDTO createIdentityUserResponseDto(
            String identityId,
            String username,
            String email,
            String firstName,
            String lastName,
            Boolean enabled,
            List<IdentityGroupDTO> groups
    ) {
        IdentityUserResponseDTO userResponseDTO = new IdentityUserResponseDTO();
        userResponseDTO.setId(identityId);
        userResponseDTO.setUsername(username);
        userResponseDTO.setEmail(email);
        userResponseDTO.setFirstName(firstName);
        userResponseDTO.setLastName(lastName);
        userResponseDTO.setEnabled(enabled);
        userResponseDTO.setGroups(groups);

        return userResponseDTO;
    }

    private IdentityGroupDTO createIdentityGroupDTO(
            String id,
            String name
    ) {
        IdentityGroupDTO groupDTO = new IdentityGroupDTO();
        groupDTO.setId(id);
        groupDTO.setName(name);

        return groupDTO;
    }
}