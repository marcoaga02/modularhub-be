package com.marcoaga02.modularhub.modules.usermanagement.mapper;

import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class LanguageMapperTest {

    private final LanguageMapper languageMapper = Mappers.getMapper(LanguageMapper.class);

    @Test
    void testToDto() {
        Language language = new Language();
        language.setCode("it-IT");
        language.setLabel("Italian");
        language.setIsDefault(true);

        LanguageResponseDTO dto = languageMapper.toDto(language);
        assertThat(dto.getId()).isEqualTo(language.getUuid());
        assertThat(dto.getCode()).isEqualTo("it-IT");
        assertThat(dto.getLabel()).isEqualTo("Italian");
        assertThat(dto.getIsDefault()).isTrue();
    }

}