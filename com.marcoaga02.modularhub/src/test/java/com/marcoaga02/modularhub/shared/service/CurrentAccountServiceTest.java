package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.constant.SecurityConstants;
import com.marcoaga02.modularhub.shared.domain.CurrentAccount;
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
class CurrentAccountServiceTest {

    @Mock
    private AccountPreferencesService accountPreferencesService;

    @InjectMocks
    private CurrentAccountService currentAccountService;

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
                List.of("USER_MANAGEMENT")
        );

        CurrentAccount account = currentAccountService.getCurrentAccount();

        assertThat(account.keycloakId()).isEqualTo("uuid-123");
        assertThat(account.email()).isEqualTo("mario@test.com");
        assertThat(account.username()).isEqualTo("mario");
        assertThat(account.roles()).containsExactly("USER_MANAGEMENT");
    }

    @Test
    void hasRole_whenRoleExists_shouldReturnTrue() {
        mockAuthentication(Map.of("sub", "uuid-123"), List.of("USER_MANAGEMENT"));
        assertThat(currentAccountService.hasRole("USER_MANAGEMENT")).isTrue();
    }

    @Test
    void hasRole_whenRoleNotExists_shouldReturnFalse() {
        mockAuthentication(Map.of("sub", "uuid-123"), List.of());
        assertThat(currentAccountService.hasRole("USER_MANAGEMENT")).isFalse();
    }

    @Test
    void hasAnyRole_whenAtLeastOneRoleExists_shouldReturnTrue() {
        mockAuthentication(Map.of("sub", "uuid-123"), List.of("USER_MANAGEMENT"));
        assertThat(currentAccountService.hasAnyRole("USER_MANAGEMENT", "ADMIN")).isTrue();
    }

    @Test
    void getJwtAuthentication_whenNoJwtAuthentication_shouldThrow() {
        SecurityContextHolder.clearContext();
        assertThatThrownBy(() -> currentAccountService.getCurrentAccount())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No JWT authentication found in security context");
    }
}