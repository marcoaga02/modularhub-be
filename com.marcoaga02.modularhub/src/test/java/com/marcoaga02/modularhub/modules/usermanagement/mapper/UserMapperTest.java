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
        Language language = buildLanguage();
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
    void updateEntityShouldUpdateAllFields() {
        Language language = buildLanguage();
        when(languageRepository.findByUuid("lang-uuid")).thenReturn(language);

        UserRequestDTO dto = buildUserRequestDTO();
        User existing = buildUser(buildLanguage());
        String originalUuid = existing.getUuid();

        userMapper.updateEntity(dto, existing);

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
    void updateEntityShouldSetNullLanguageWhenLanguageIdIsNull() {
        UserRequestDTO dto = buildUserRequestDTO();
        dto.setLanguageId(null);

        User existing = buildUser(buildLanguage());

        userMapper.updateEntity(dto, existing);

        assertThat(existing.getLanguage()).isNull();
        verifyNoInteractions(languageRepository);
    }

    private Language buildLanguage() {
        Language language = new Language();
        language.setCode("it-IT");
        language.setLabel("Italian");
        language.setIsDefault(true);
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