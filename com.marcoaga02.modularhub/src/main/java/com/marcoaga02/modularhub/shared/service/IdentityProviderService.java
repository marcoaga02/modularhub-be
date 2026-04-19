package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.dto.IdentityGroupDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserCreateRequestDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserUpdateRequestDTO;

import java.util.List;
import java.util.Set;

public interface IdentityProviderService {

    // TODO inserire eccezione personalizzata IdentityProviderException e mettere il throws ai metodi
    //  (o forse senza throws se la faccio unchecked)

    String createUser(IdentityUserCreateRequestDTO user);

    void updateUser(String userId, IdentityUserUpdateRequestDTO user);

    void deleteUser(String userId);

    IdentityUserResponseDTO getUserById(String userId);

    List<IdentityGroupDTO> getGroups();

    List<IdentityGroupDTO> getUserGroups(String userId);

    void updateUserGroups(String userId, Set<String> groupIds);

    void resetPassword(String userId, String password);
}
