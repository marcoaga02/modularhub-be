package com.marcoaga02.modularhub.modules.usermanagement.mapper;

import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class LanguageMapper {

    @Autowired
    LanguageRepository languageRepository;

    @Mapping(source = "uuid", target = "id")
    public abstract LanguageResponseDTO toDto(Language language);

    public Language fromUuid(String uuid) {
        return Optional.ofNullable(uuid)
                .map(languageRepository::findByUuid)
                .orElse(null);
    }

}
