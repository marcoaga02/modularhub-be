package com.marcoaga02.modularhub.shared.mapper;

import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;
import com.marcoaga02.modularhub.shared.model.AccountPreferences;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = LanguageMapper.class)
public interface AccountPreferencesMapper {

    AccountPreferencesResponseDTO toDto(AccountPreferences accountPreferences);

}
