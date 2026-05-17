package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.dto.IdentityGroupDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserCreateRequestDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserUpdateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class IdentityService {

    private final IdentityProviderService identityProvider;

    public IdentityService(IdentityProviderService identityProvider) {
        this.identityProvider = identityProvider;
    }

    public String createUser(IdentityUserCreateRequestDTO user) {
        String userId = identityProvider.createUser(user);

        if (StringUtils.hasText(user.getPassword())) {
            identityProvider.updatePassword(userId, user.getPassword());
        }

        return userId;
    }

    public void updateUser(String userId, IdentityUserUpdateRequestDTO user) {
        identityProvider.updateUser(userId, user);
    }

    public IdentityUserResponseDTO getUserById(String id) {
        return identityProvider.getUserById(id);
    }

    public void resetPassword(String userId) {
        identityProvider.resetPassword(userId);
    }

    public void deleteUser(String userId) {
        identityProvider.deleteUser(userId);
    }

    public List<IdentityGroupDTO> getGroups() {
        return identityProvider.getGroups();
    }
}