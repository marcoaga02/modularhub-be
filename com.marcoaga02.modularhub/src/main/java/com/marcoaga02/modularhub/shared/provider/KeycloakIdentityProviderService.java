package com.marcoaga02.modularhub.shared.provider;

import com.marcoaga02.modularhub.shared.dto.IdentityGroupDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserCreateRequestDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserUpdateRequestDTO;
import com.marcoaga02.modularhub.shared.service.IdentityProviderService;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO testare

@Service
@Profile("keycloak")
public class KeycloakIdentityProviderService implements IdentityProviderService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakIdentityProviderService(
            Keycloak keycloak,
            @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    private org.keycloak.admin.client.resource.RealmResource realm() {
        return keycloak.realm(realm);
    }

    @Override
    public String createUser(IdentityUserCreateRequestDTO user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEnabled(user.getEnabled());
        userRepresentation.setEmailVerified(Boolean.TRUE.equals(user.getEmailVerified()));
        userRepresentation.setGroups(user.getGroupIds());

        try (Response response = realm().users().create(userRepresentation)) {
            checkResponse(response);
            return CreatedResponseUtil.getCreatedId(response);
        }
    }

    @Override
    public void updateUser(String userId, IdentityUserUpdateRequestDTO user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEnabled(user.getEnabled());

        realm().users().get(userId).update(userRepresentation);

        if (user.getGroupIds() != null) {
            updateUserGroups(userId, new HashSet<>(user.getGroupIds()));
        }
    }

    @Override
    public void deleteUser(String userId) {
        try (Response response = realm().users().delete(userId)) {
            checkResponse(response);
        }
    }

    @Override
    public IdentityUserResponseDTO getUserById(String userId) {
        UserRepresentation kcUser =
                realm().users().get(userId).toRepresentation();

        final String identityId = kcUser.getId();

        IdentityUserResponseDTO user = new IdentityUserResponseDTO();
        user.setId(identityId);
        user.setUsername(kcUser.getUsername());
        user.setEmail(kcUser.getEmail());
        user.setFirstName(kcUser.getFirstName());
        user.setLastName(kcUser.getLastName());
        user.setEnabled(kcUser.isEnabled());
        user.setGroups(getUserGroups(identityId));

        return user;
    }

    @Override
    public List<IdentityGroupDTO> getGroups() {
        return realm().groups().groups().stream()
                .map(g -> new IdentityGroupDTO(g.getId(), g.getName()))
                .toList();
    }

    @Override
    public List<IdentityGroupDTO> getUserGroups(String userId) {
        return realm().users().get(userId).groups().stream()
                .map(g -> new IdentityGroupDTO(g.getId(), g.getName()))
                .toList();
    }

    @Override
    public void updateUserGroups(String userId, Set<String> groupIds) {
        var userResource = realm().users().get(userId);

        Set<String> currentGroups = userResource.groups().stream()
                .map(GroupRepresentation::getId)
                .collect(Collectors.toSet());

        currentGroups.stream()
                .filter(id -> !groupIds.contains(id))
                .forEach(userResource::leaveGroup);

        groupIds.stream()
                .filter(id -> !currentGroups.contains(id))
                .forEach(userResource::joinGroup);
    }

    @Override
    public void resetPassword(String userId, String password) {
        CredentialRepresentation credential =
                new CredentialRepresentation();
        credential.setTemporary(true);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        realm().users().get(userId).resetPassword(credential);
    }

    private void checkResponse(Response response) {
        Response.Status.Family family =
                response.getStatusInfo().getFamily();

        if (family == Response.Status.Family.CLIENT_ERROR ||
                family == Response.Status.Family.SERVER_ERROR) {

            String errorMessage = response.readEntity(String.class);
            throw new ResponseStatusException(
                    HttpStatus.valueOf(response.getStatus()),
                    errorMessage
            );
        }
    }
}