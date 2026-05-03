package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.dto.IdentityGroupDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final IdentityService identityService;

    public GroupService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public List<IdentityGroupDTO> getAllGroups() {
        return this.identityService.getGroups();
    }
}
