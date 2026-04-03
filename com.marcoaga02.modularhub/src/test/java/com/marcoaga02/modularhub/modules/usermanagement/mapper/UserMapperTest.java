package com.marcoaga02.modularhub.modules.usermanagement.mapper;

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

@ExtendWith(SpringExtension.class)
@Import({UserMapperImpl.class, LanguageMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @MockitoBean
    private LanguageRepository languageRepository;

    @Test
    void testToDto() {
        Language language = new Language();
        language.setCode("it-IT");
        language.setLabel("Italian");
        language.setIsDefault(true);

        User user = new User();
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setGender(Gender.M);
        user.setLanguage(language);
        user.setMobileNumber("0000");
        user.setTaxIdNumber("USR1");
        user.setEmail("user@email.com");
        user.setEnabled(true);

        UserResponseDTO dto = userMapper.toDto(user);

        assertThat(dto.getFirstname()).isEqualTo("firstname");
        assertThat(dto.getLastname()).isEqualTo("lastname");
        assertThat(dto.getGender()).isEqualTo(Gender.M.name());
        assertThat(dto.getLanguage().getId()).isEqualTo(language.getUuid());
        assertThat(dto.getLanguage().getCode()).isEqualTo(language.getCode());
        assertThat(dto.getLanguage().getLabel()).isEqualTo(language.getLabel());
        assertThat(dto.getLanguage().getIsDefault()).isTrue();
        assertThat(dto.getMobileNumber()).isEqualTo("0000");
        assertThat(dto.getTaxIdNumber()).isEqualTo("USR1");
        assertThat(dto.getEmail()).isEqualTo("user@email.com");
        assertThat(dto.getEnabled()).isTrue();
    }

}