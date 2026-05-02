package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.dto.IdentityGroupDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserCreateRequestDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserUpdateRequestDTO;

import java.util.List;

public interface IdentityProviderService {

    String createUser(IdentityUserCreateRequestDTO user);

    void updateUser(String userId, IdentityUserUpdateRequestDTO user);

    void deleteUser(String userId);

    IdentityUserResponseDTO getUserById(String userId);

    List<IdentityGroupDTO> getGroups();

    List<IdentityGroupDTO> getUserGroups(String userId);

    void resetPassword(String userId, String password);
}
