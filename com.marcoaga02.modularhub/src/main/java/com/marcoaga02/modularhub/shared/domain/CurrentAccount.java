package com.marcoaga02.modularhub.shared.domain;

import java.util.Set;

public record CurrentAccount(
        String keycloakId,
        String email,
        String username,
        Set<String> roles
) {}
