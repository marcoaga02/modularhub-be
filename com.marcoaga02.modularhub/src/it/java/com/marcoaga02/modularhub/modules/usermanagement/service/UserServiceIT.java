package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.ModularhubApplication;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Gender;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import com.marcoaga02.modularhub.modules.usermanagement.repository.UserRepository;
import com.marcoaga02.modularhub.shared.exceptions.BadRequestException;
import com.marcoaga02.modularhub.shared.exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = ModularhubApplication.class)
@Transactional
class UserServiceIT {

    public static final String TAX_ID_NUMBER = "USR_1";
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private EntityManager entityManager;

    private User enabledUser, disabledUser, anotherUser;
    
    private Language lang1;

    @BeforeEach
    void setUp() {
        lang1 = createLanguage("it-IT", "Italiano", true);
        Language lang2 = createLanguage("en-US", "English", false);

        enabledUser = createUser(
                "firstname1",
                "lastname1",
                Gender.M,
                lang1,
                "0000",
                TAX_ID_NUMBER,
                "enabled@email.com",
                true
        );

        disabledUser = createUser(
                "firstname2",
                "lastname2",
                Gender.F,
                lang2,
                "1111",
                "USR_2",
                "disabled@email.com",
                false
        );

        anotherUser = createUser(
                "firstname3",
                "lastname3",
                Gender.F,
                lang2,
                "2222",
                "USR_3",
                "another@email.com",
                true
        );

        User deletedUser = createUser(
                "firstname4",
                "lastname4",
                Gender.M,
                lang2,
                "3333",
                "USR_4",
                "deleted@email.com",
                true
        );
        deletedUser.setDeletedOn(OffsetDateTime.now());

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testGetAllUsers() {
        Page<UserResponseDTO> result = userService.getAllUsers(
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getTotalPages()).isEqualTo(1);

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactlyInAnyOrder(
                        enabledUser.getUuid(),
                        disabledUser.getUuid(),
                        anotherUser.getUuid()
                );
    }

    @Test
    void testGetAllUsers_whenPageSizeIsOne_shouldReturnOnlyOneUser() {
        Page<UserResponseDTO> result = userService.getAllUsers(
                null,
                PageRequest.of(0, 1, Sort.by("firstname").ascending())
        );

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(1);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(
                        enabledUser.getUuid()
                );
    }

    @Test
    void testGetAllUsers_whenPageSizeIsTwo_shouldReturnTwoUser() {
        Page<UserResponseDTO> result = userService.getAllUsers(
                null,
                PageRequest.of(0, 2, Sort.by("firstname").ascending())
        );

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(2);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(
                        enabledUser.getUuid(),
                        disabledUser.getUuid()
                );
    }

    @Test
    void testGetAllUsers_whenSecondPage_shouldReturnCorrectUsers() {
        Page<UserResponseDTO> result = userService.getAllUsers(
                null,
                PageRequest.of(1, 2, Sort.by("firstname").ascending())
        );

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(anotherUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByText_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("firstname1");

        Page<UserResponseDTO> result = userService.getAllUsers(
                criteria,
                PageRequest.of(0, 10)
        );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(enabledUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByTextMatchesLastname_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("lastname2");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(disabledUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByTextMatchesEmail_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("another@email.com");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(anotherUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByFirstnameCaseInsensitive_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("FIRSTNAME1");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(enabledUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByLastnameCaseInsensitive_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("LASTNAME2");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(disabledUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByEmailCaseInsensitive_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("ANOTHER@EMAIL.COM");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(anotherUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByPartialFirstname_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("irstname1");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(enabledUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByPartialLastname_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("astname2");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(disabledUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByPartialEmail_shouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("another");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(anotherUser.getUuid());
    }

    @Test
    void testGetAllUsers_whenFilterByTextWithNoMatch_shouldReturnEmptyPage() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("nonexistent");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testGetAllUsers_whenFilterByEmptyText_shouldReturnAllUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testGetUserByUuid_whenUserExists_shouldReturnUser() {
        UserResponseDTO result = userService.getUserByUuid(enabledUser.getUuid());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(enabledUser.getUuid());
    }

    @Test
    void testGetUserByUuid_whenUserNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.getUserByUuid("non-existing-uuid"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'non-existing-uuid' not found");
    }

    @Test
    void testCreateUser_whenTaxIdNumberNotExists_shouldCreateAndReturnDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstname("New firstname");
        dto.setLastname("New lastname");
        dto.setGender(Gender.M);
        dto.setLanguageId(lang1.getUuid());
        dto.setMobileNumber("4444");
        dto.setTaxIdNumber("NEW_TAX_ID");
        dto.setEmail("new@email.com");
        dto.setUsername("newusername");
        dto.setPassword("newpassword");
        dto.setEnabled(true);

        UserResponseDTO result = userService.createUser(dto);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTaxIdNumber()).isEqualTo("NEW_TAX_ID");
    }

    @Test
    void testCreateUser_whenInvalidLanguageId_shouldThrowBadRequestException() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstname("New firstname");
        dto.setLastname("New lastname");
        dto.setGender(Gender.M);
        dto.setLanguageId("NON-EXIST-LANG");
        dto.setMobileNumber("4444");
        dto.setTaxIdNumber("NEW_TAX_ID");
        dto.setEmail("new@email.com");
        dto.setUsername("newusername");
        dto.setPassword("newpassword");
        dto.setEnabled(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language with uuid 'NON-EXIST-LANG' not found");
    }

    private User createUser(String firstname,
                            String lastname,
                            Gender gender,
                            Language language,
                            String mobileNumber,
                            String taxIdNumber,
                            String email,
                            Boolean enabled
    ) {
        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setGender(gender);
        user.setLanguage(language);
        user.setMobileNumber(mobileNumber);
        user.setTaxIdNumber(taxIdNumber);
        user.setEmail(email);
        user.setEnabled(enabled);

        userRepository.save(user);

        return user;
    }

    private Language createLanguage(String code, String label, boolean isDefault) {
        Language language = new Language();
        language.setCode(code);
        language.setLabel(label);
        language.setIsDefault(isDefault);

        languageRepository.save(language);

        return language;
    }

}