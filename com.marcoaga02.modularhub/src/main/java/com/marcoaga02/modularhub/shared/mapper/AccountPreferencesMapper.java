package com.marcoaga02.modularhub.shared.mapper;

import com.marcoaga02.modularhub.shared.dto.AccountPreferencesCreateDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesUpdateDTO;
import com.marcoaga02.modularhub.shared.model.AccountPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {LanguageMapper.class})
public interface AccountPreferencesMapper {

    AccountPreferencesResponseDTO toDto(AccountPreferences accountPreferences);

    @Mapping(source = "languageId", target = "language")
    AccountPreferences toEntity(AccountPreferencesCreateDTO dto, @MappingTarget AccountPreferences accountPreferences);

    @Mapping(target = "identityId", ignore = true)
    @Mapping(source = "languageId", target = "language")
    AccountPreferences updateEntity(AccountPreferencesUpdateDTO dto, @MappingTarget AccountPreferences accountPreferences);

}
