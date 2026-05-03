package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.constant.SecurityConstants;
import com.marcoaga02.modularhub.shared.dto.AccountDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountPreferencesService accountPreferencesService;

    @InjectMocks
    private AccountService accountService;

    private void mockAuthentication(Map<String, Object> claims, List<String> roles) {
        Map<String, Object> headers = Map.of("alg", "RS256");
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(300), headers, claims);

        List<GrantedAuthority> authorities = roles.stream()
                .map(r -> (GrantedAuthority) new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + r))
                .toList();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentAccount_shouldReturnCorrectAccount() {
        mockAuthentication(
                Map.of("sub", "uuid-123", "email", "mario@test.com", "preferred_username", "mario"),
                List.of("ROLE")
        );

        AccountDTO account = accountService.getCurrentAccount();

        assertThat(account.getIdentityId()).isEqualTo("uuid-123");
        assertThat(account.getEmail()).isEqualTo("mario@test.com");
        assertThat(account.getUsername()).isEqualTo("mario");
        assertThat(account.getRoles()).containsExactly("ROLE");
    }

    @Test
    void hasRole_shouldReturnTrue_whenRoleExists() {
        mockAuthentication(Map.of("sub", "uuid-123"), List.of("ROLE"));
        assertThat(accountService.hasRole("ROLE")).isTrue();
    }

    @Test
    void hasRole_shouldReturnFalse_whenRoleNotExists() {
        mockAuthentication(Map.of("sub", "uuid-123"), List.of());
        assertThat(accountService.hasRole("ROLE")).isFalse();
    }

    @Test
    void hasAnyRole_shouldReturnTrue_whenAtLeastOneRoleExists() {
        mockAuthentication(Map.of("sub", "uuid-123"), List.of("ROLE"));
        assertThat(accountService.hasAnyRole("ROLE", "ADMIN")).isTrue();
    }

    @Test
    void getJwtAuthentication_shouldThrowIllegalStateException_whenNoJwtAuthentication() {
        SecurityContextHolder.clearContext();
        assertThatThrownBy(() -> accountService.getCurrentAccount())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No JWT authentication found in security context");
    }
}