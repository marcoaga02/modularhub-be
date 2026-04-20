package com.marcoaga02.modularhub.shared.domain;

import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;

import java.util.Set;

public record CurrentAccount(
        String keycloakId,
        String email,
        String username,
        Set<String> roles,
        AccountPreferencesResponseDTO preferences
) {}
