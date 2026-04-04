package com.marcoaga02.modularhub.modules.usermanagement.mapper;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Gender;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
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
@Import({UserMapperImpl.class, LanguageMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @MockitoBean
    private LanguageRepository languageRepository;

    @Test
    void testToDtoShouldMapAllFields() {
        Language language = buildLanguage("it-IT", "Italian", true);
        User user = buildUser(language);

        UserResponseDTO dto = userMapper.toDto(user);

        assertThat(dto.getFirstname()).isEqualTo("firstname");
        assertThat(dto.getLastname()).isEqualTo("lastname");
        assertThat(dto.getGender()).isEqualTo(Gender.M.name());
        assertThat(dto.getLanguage().getId()).isEqualTo(language.getUuid());
        assertThat(dto.getLanguage().getCode()).isEqualTo("it-IT");
        assertThat(dto.getLanguage().getLabel()).isEqualTo("Italian");
        assertThat(dto.getLanguage().getIsDefault()).isTrue();
        assertThat(dto.getMobileNumber()).isEqualTo("0000");
        assertThat(dto.getTaxIdNumber()).isEqualTo("USR1");
        assertThat(dto.getEmail()).isEqualTo("user@email.com");
        assertThat(dto.getEnabled()).isTrue();
    }

    @Test
    void testToDtoWhenInputIsNullShouldReturnNull() {
        assertThat(userMapper.toDto(null)).isNull();
    }

    @Test
    void updateEntityShouldUpdateAllFields() {
        Language language = buildLanguage("it-IT", "Italian", true);
        when(languageRepository.findByUuid("lang-uuid")).thenReturn(language);

        UserRequestDTO dto = buildUserRequestDTO();
        User existing = buildUser(buildLanguage("en-EN", "English", false));
        String originalUuid = existing.getUuid();

        User result = userMapper.updateEntity(dto, existing);
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(existing);

        assertThat(existing.getFirstname()).isEqualTo("new firstname");
        assertThat(existing.getLastname()).isEqualTo("new lastname");
        assertThat(existing.getGender()).isEqualTo(Gender.F);
        assertThat(existing.getLanguage()).isEqualTo(language);
        assertThat(existing.getMobileNumber()).isEqualTo("9999");
        assertThat(existing.getTaxIdNumber()).isEqualTo("NEW1");
        assertThat(existing.getEmail()).isEqualTo("new@email.com");
        assertThat(existing.getEnabled()).isFalse();
        assertThat(existing.getUuid()).isEqualTo(originalUuid);
    }

    @Test
    void tesUpdateEntityWhenInputDtoIsNullShouldReturnUnmodifiedUser() {
        when(languageRepository.findByUuid("lang-uuid"))
                .thenReturn(buildLanguage("it-IT", "Italian", true));

        Language language = buildLanguage("en-EN", "English", false);
        User existing = buildUser(language);
        String originalUuid = existing.getUuid();

        User result = userMapper.updateEntity(null, existing);
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(existing);

        assertThat(existing.getFirstname()).isEqualTo("firstname");
        assertThat(existing.getLastname()).isEqualTo("lastname");
        assertThat(existing.getGender()).isEqualTo(Gender.M);
        assertThat(existing.getLanguage()).isEqualTo(language);
        assertThat(existing.getMobileNumber()).isEqualTo("0000");
        assertThat(existing.getTaxIdNumber()).isEqualTo("USR1");
        assertThat(existing.getEmail()).isEqualTo("user@email.com");
        assertThat(existing.getEnabled()).isTrue();
        assertThat(existing.getUuid()).isEqualTo(originalUuid);
    }

    @Test
    void updateEntityShouldSetNullLanguageWhenLanguageIdIsNull() {
        UserRequestDTO dto = buildUserRequestDTO();
        dto.setLanguageId(null);

        User existing = buildUser(buildLanguage("it-IT", "Italian", true));

        User result = userMapper.updateEntity(dto, existing);
        assertThat(result).isSameAs(existing);

        assertThat(existing.getLanguage()).isNull();
        verifyNoInteractions(languageRepository);
    }

    private Language buildLanguage(String code, String label, Boolean isDefault) {
        Language language = new Language();
        language.setCode(code);
        language.setLabel(label);
        language.setIsDefault(isDefault);
        return language;
    }

    private User buildUser(Language language) {
        User user = new User();
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setGender(Gender.M);
        user.setLanguage(language);
        user.setMobileNumber("0000");
        user.setTaxIdNumber("USR1");
        user.setEmail("user@email.com");
        user.setEnabled(true);
        return user;
    }

    private UserRequestDTO buildUserRequestDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstname("new firstname");
        dto.setLastname("new lastname");
        dto.setGender(Gender.F);
        dto.setLanguageId("lang-uuid");
        dto.setMobileNumber("9999");
        dto.setTaxIdNumber("NEW1");
        dto.setEmail("new@email.com");
        dto.setUsername("new username");
        dto.setEnabled(false);
        return dto;
    }

}