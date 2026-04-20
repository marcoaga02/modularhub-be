package com.marcoaga02.modularhub.shared.dto;

import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AccountPreferencesResponseDTO {

    private LanguageResponseDTO language;

}
