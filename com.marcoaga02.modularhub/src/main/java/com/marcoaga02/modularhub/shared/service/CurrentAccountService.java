package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.constant.KeycloakClaims;
import com.marcoaga02.modularhub.shared.constant.SecurityConstants;
import com.marcoaga02.modularhub.shared.domain.CurrentAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequestScope
public class CurrentAccountService {

    private JwtAuthenticationToken getJwtAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth;
        }
        throw new IllegalStateException("No JWT authentication found in security context");
    }

    public CurrentAccount getCurrentAccount() {
        Jwt jwt = getJwtAuthentication().getToken();
        return new CurrentAccount(
                jwt.getSubject(),
                jwt.getClaimAsString(KeycloakClaims.EMAIL),
                jwt.getClaimAsString(KeycloakClaims.USERNAME),
                getRoles()
        );
    }

    public Set<String> getRoles() {
        return getJwtAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .filter(a -> a.startsWith(SecurityConstants.ROLE_PREFIX))
                .map(a -> a.substring(SecurityConstants.ROLE_PREFIX.length()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        Set<String> userRoles = getRoles();
        return Arrays.stream(roles).anyMatch(userRoles::contains);
    }
}
