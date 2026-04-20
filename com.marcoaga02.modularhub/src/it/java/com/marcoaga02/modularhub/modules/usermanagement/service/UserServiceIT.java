package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.ModularhubApplication;
import com.marcoaga02.modularhub.config.BaseITClass;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Gender;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import com.marcoaga02.modularhub.modules.usermanagement.repository.UserRepository;
import com.marcoaga02.modularhub.shared.domain.CurrentAccount;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.exception.BadRequestException;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import com.marcoaga02.modularhub.shared.model.AccountPreferences;
import com.marcoaga02.modularhub.shared.model.Language;
import com.marcoaga02.modularhub.shared.repository.AccountPreferencesRepository;
import com.marcoaga02.modularhub.shared.service.AccountPreferencesService;
import com.marcoaga02.modularhub.shared.service.CurrentAccountService;
import com.marcoaga02.modularhub.shared.service.IdentityService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ModularhubApplication.class)
@Transactional
class UserServiceIT extends BaseITClass {

    public static final String TAX_ID_NUMBER = "USR_1";

    @MockitoBean
    private IdentityService identityService;

    @MockitoBean
    private CurrentAccountService currentAccountService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountPreferencesService accountPreferencesService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private AccountPreferencesRepository accountPreferencesRepository;

    @Autowired
    private EntityManager entityManager;

    private User enabledUser, disabledUser, anotherUser;

    private Language lang1, lang2;

    @BeforeEach
    void setUp() {
        CurrentAccount currentAccount = new CurrentAccount(
                "current-id",
                "current@email.com",
                "current.username",
                Set.of(),
                new AccountPreferencesResponseDTO()
        );

        when(currentAccountService.getCurrentAccount())
                .thenReturn(currentAccount);

        lang1 = createLanguage("it-IT", "Italiano", true);
        lang2 = createLanguage("en-US", "English", false);

        enabledUser = createUser(
                "identity1",
                "firstname1",
                "lastname1",
                Gender.M,
                "0000",
                TAX_ID_NUMBER,
                "enabled@email.com",
                "username1",
                true
        );

        disabledUser = createUser(
                "identity2",
                "firstname2",
                "lastname2",
                Gender.F,
                "1111",
                "USR_2",
                "disabled@email.com",
                "username2",
                false
        );

        anotherUser = createUser(
                "identity3",
                "firstname3",
                "lastname3",
                Gender.F,
                "2222",
                "USR_3",
                "another@email.com",
                "username3",
                true
        );

        User deletedUser = createUser(
                "identity4",
                "firstname4",
                "lastname4",
                Gender.M,
                "3333",
                "USR_4",
                "deleted@email.com",
                "username4",
                true
        );
        deletedUser.setDeletedOn(OffsetDateTime.now());

        createAccountPreferences("identity1", lang1);
        createAccountPreferences("identity2", lang2);
        createAccountPreferences("identity3", lang2);

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
    void testGetAllUsersWhenPageSizeIsOneShouldReturnOnlyOneUser() {
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
    void testGetAllUsersWhenPageSizeIsTwoShouldReturnTwoUser() {
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
    void testGetAllUsersWhenSecondPageShouldReturnCorrectUsers() {
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
    void testGetAllUsersWhenFilterByTextShouldReturnMatchingUsers() {
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
    void testGetAllUsersWhenFilterByTextMatchesLastnameShouldReturnMatchingUsers() {
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
    void testGetAllUsersWhenFilterByTextMatchesEmailShouldReturnMatchingUsers() {
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
    void testGetAllUsersWhenFilterByFirstnameCaseInsensitiveShouldReturnMatchingUsers() {
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
    void testGetAllUsersWhenFilterByLastnameCaseInsensitiveShouldReturnMatchingUsers() {
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
    void testGetAllUsersWhenFilterByEmailCaseInsensitiveShouldReturnMatchingUsers() {
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
    void testGetAllUsersWhenFilterByPartialFirstnameShouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("irstname1");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(enabledUser.getUuid());
    }

    @Test
    void testGetAllUsersWhenFilterByPartialLastnameShouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("astname2");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(disabledUser.getUuid());
    }

    @Test
    void testGetAllUsersWhenFilterByPartialEmailShouldReturnMatchingUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("another");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(UserResponseDTO::getId)
                .containsExactly(anotherUser.getUuid());
    }

    @Test
    void testGetAllUsersWhenFilterByTextWithNoMatchShouldReturnEmptyPage() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("nonexistent");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testGetAllUsersWhenFilterByEmptyTextShouldReturnAllUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        criteria.setText("");

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testGetUserByUuidWhenUserExistsShouldReturnUser() {
        when(identityService.getUserById(any()))
                .thenReturn(new IdentityUserResponseDTO());

        UserResponseDTO result = userService.getUserByUuid(enabledUser.getUuid());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(enabledUser.getUuid());
    }

    @Test
    void testGetUserByUuidWhenUserNotFoundShouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.getUserByUuid("non-existing-uuid"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'non-existing-uuid' not found");
    }

    @Test
    void testCreateUserWhenTaxIdNumberNotExistsShouldCreateAndReturnDTO() {
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
    void testCreateUserWhenInvalidLanguageIdShouldThrowBadRequestException() {
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

    @Test
    void testUpdateUserWhenValidShouldUpdateAndReturnDTO() {
        UserRequestDTO dto = buildUserRequestDTO(lang2.getUuid(), enabledUser.getTaxIdNumber());

        UserResponseDTO result = userService.updateUser(enabledUser.getUuid(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(enabledUser.getUuid());
        assertThat(result.getFirstname()).isEqualTo("new firstname");
        assertThat(result.getLastname()).isEqualTo("new lastname");
        assertThat(result.getGender()).isEqualTo(Gender.F.name());
        assertThat(result.getMobileNumber()).isEqualTo("9999");
        assertThat(result.getTaxIdNumber()).isEqualTo(enabledUser.getTaxIdNumber());
        assertThat(result.getEmail()).isEqualTo("new@email.com");
        assertThat(result.getEnabled()).isFalse();
        assertThat(result.getLanguage().getId()).isEqualTo(lang2.getUuid());
    }

    @Test
    void testUpdateUserWhenTaxIdNumberChangedAndNotExistsShouldUpdateAndReturnDTO() {
        UserRequestDTO dto = buildUserRequestDTO(lang1.getUuid(), "TAX_ID_NEW");

        UserResponseDTO result = userService.updateUser(enabledUser.getUuid(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(enabledUser.getUuid());
        assertThat(result.getTaxIdNumber()).isEqualTo("TAX_ID_NEW");
    }

    @Test
    void testUpdateUserWhenUserNotFoundShouldThrowNotFoundException() {
        UserRequestDTO dto = buildUserRequestDTO(lang1.getUuid(), "TAX_ID_NEW");

        assertThatThrownBy(() -> userService.updateUser("non-existing-uuid", dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'non-existing-uuid' not found");
    }

    @Test
    void testUpdateUserWhenInvalidLanguageShouldThrowBadRequestException() {
        UserRequestDTO dto = buildUserRequestDTO("NON-EXIST-LANG", enabledUser.getTaxIdNumber());

        assertThatThrownBy(() -> userService.updateUser(enabledUser.getUuid(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language with uuid 'NON-EXIST-LANG' not found");
    }

    @Test
    void testUpdateUserWhenTaxIdNumberChangedAndAlreadyExistsShouldThrowBadRequestException() {
        UserRequestDTO dto = buildUserRequestDTO(lang1.getUuid(), anotherUser.getTaxIdNumber());

        assertThatThrownBy(() -> userService.updateUser(enabledUser.getUuid(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.format("User with taxIdNumber '%s' already exists", anotherUser.getTaxIdNumber()));
    }

    @Test
    void testDeleteUserWhenUserExistsShouldSoftDelete() {
        User existing = userRepository.findByUuidAndDeletedOnIsNull(enabledUser.getUuid())
                .orElseThrow();

        assertThat(existing.getDeletedOn()).isNull();

        userService.deleteUser(enabledUser.getUuid());

        entityManager.flush();
        entityManager.clear();

        User deleted = userRepository.findById(enabledUser.getId()).orElseThrow();
        assertThat(deleted.getDeletedBy()).isEqualTo("current-id");
        assertThat(deleted.getDeletedOn()).isNotNull();
    }

    @Test
    void testDeleteUserWhenUserNotFoundShouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.deleteUser("non-existing-uuid"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'non-existing-uuid' not found");
    }

    private User createUser(
            String identityId,
            String firstname,
            String lastname,
            Gender gender,
            String mobileNumber,
            String taxIdNumber,
            String email,
            String username,
            Boolean enabled
    ) {
        User user = new User();
        user.setIdentityId(identityId);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setGender(gender);
        user.setMobileNumber(mobileNumber);
        user.setTaxIdNumber(taxIdNumber);
        user.setEmail(email);
        user.setUsername(username);
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

    private UserRequestDTO buildUserRequestDTO(String languageId, String taxIdNumber) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstname("new firstname");
        dto.setLastname("new lastname");
        dto.setGender(Gender.F);
        dto.setLanguageId(languageId);
        dto.setMobileNumber("9999");
        dto.setTaxIdNumber(taxIdNumber);
        dto.setEmail("new@email.com");
        dto.setUsername("new username");
        dto.setPassword("newpassword");
        dto.setEnabled(false);
        return dto;
    }

    private void createAccountPreferences(
            String identityId,
            Language language
    ) {
        AccountPreferences preferences = new AccountPreferences();
        preferences.setIdentityId(identityId);
        preferences.setLanguage(language);

        accountPreferencesRepository.save(preferences);

    }

}