package com.marcoaga02.modularhub.modules.usermanagement.mapper;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.shared.dto.AuditDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "audit", expression = "java(mapAudit(user))")
    @Mapping(source = "uuid", target = "id")
    UserResponseDTO toDto(User user);

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "deletedOn", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "identityId", ignore = true)
    User updateEntity(UserRequestDTO dto, @MappingTarget User user);

    // TODO aggiornare mapping in modo da mettere fullname dell'utente che ha creato/aggiornato, non il suo identityId
    default AuditDTO mapAudit(User user) {
        AuditDTO dto = new AuditDTO();
        dto.setCreatedBy(user.getCreatedBy());
        dto.setCreatedOn(user.getCreatedOn());
        dto.setUpdatedBy(user.getUpdatedBy());
        dto.setUpdatedOn(user.getUpdatedOn());

        return dto;
    }

}
