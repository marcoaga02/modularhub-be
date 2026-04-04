package com.marcoaga02.modularhub.modules.usermanagement.mapper;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {LanguageMapper.class})
public interface UserMapper {

    // TODO aggiungere mapping per AuditDTO una volta implementato keycloak
    @Mapping(source = "uuid", target = "id")
    UserResponseDTO toDto(User user);

    @Mapping(source = "languageId", target = "language")
    void updateEntity(UserRequestDTO dto, @MappingTarget User user);

}
