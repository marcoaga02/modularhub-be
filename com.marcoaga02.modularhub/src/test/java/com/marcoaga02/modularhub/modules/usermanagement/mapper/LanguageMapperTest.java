package com.marcoaga02.modularhub.modules.usermanagement.mapper;

import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(LanguageMapperImpl.class)
class LanguageMapperTest {

    @Autowired
    private LanguageMapper languageMapper;

    @MockitoBean
    private LanguageRepository languageRepository;

    @Test
    void testToDtoShouldMapAllFields() {
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

    @Test
    void testToDtoWhenInputIsNullShouldReturnNull() {
        assertThat(languageMapper.toDto(null)).isNull();
    }

    @Test
    void testFromUuidShouldReturnLanguageWhenUuidExists() {
        Language language = new Language();
        when(languageRepository.findByUuid("lang-uuid")).thenReturn(language);

        assertThat(languageMapper.fromUuid("lang-uuid")).isEqualTo(language);
    }

    @Test
    void testFromUuidShouldReturnNullWhenUuidIsNull() {
        assertThat(languageMapper.fromUuid(null)).isNull();

        verifyNoInteractions(languageRepository);
    }

    @Test
    void testFromUuidShouldReturnNullWhenUuidNotFound() {
        when(languageRepository.findByUuid("unknown")).thenReturn(null);

        assertThat(languageMapper.fromUuid("unknown")).isNull();
    }
}