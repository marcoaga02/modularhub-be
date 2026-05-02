package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.ModularhubApplication;
import com.marcoaga02.modularhub.config.BaseITWithMockIdentity;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Gender;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.repository.UserRepository;
import com.marcoaga02.modularhub.shared.dto.AccountDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.exception.BadRequestException;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import com.marcoaga02.modularhub.shared.model.AccountPreferences;
import com.marcoaga02.modularhub.shared.model.Language;
import com.marcoaga02.modularhub.shared.repository.AccountPreferencesRepository;
import com.marcoaga02.modularhub.shared.repository.LanguageRepository;
import com.marcoaga02.modularhub.shared.service.AccountPreferencesService;
import com.marcoaga02.modularhub.shared.service.AccountService;
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
class UserServiceIT extends BaseITWithMockIdentity {

    private static final String TAX_ID_NUMBER = "USR_1";

    @MockitoBean
    private IdentityService identityService;

    @MockitoBean
    private AccountService accountService;

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
        AccountDTO currentAccount = new AccountDTO(
                "current-id",
                "current@email.com",
                "current.username",
                "currentFirstName",
                "currentLastName",
                Set.of(),
                new AccountPreferencesResponseDTO()
        );

        when(accountService.getCurrentAccount())
                .thenReturn(currentAccount);

        lang1 = createAndSaveLanguage("it-IT", "Italiano", true);
        lang2 = createAndSaveLanguage("en-US", "English", false);

        enabledUser = createAndSaveUser(
                "identity1",
                "firstname1",
                "lastname1",
                Gender.M,
                "0000",
                TAX_ID_NUMBER,
                "enabled@email.com",
                "username1",
                true,
                null
        );

        disabledUser = createAndSaveUser(
                "identity2",
                "firstname2",
                "lastname2",
                Gender.F,
                "1111",
                "USR_2",
                "disabled@email.com",
                "username2",
                false,
                null
        );

        anotherUser = createAndSaveUser(
                "identity3",
                "firstname3",
                "lastname3",
                Gender.F,
                "2222",
                "USR_3",
                "another@email.com",
                "username3",
                true,
                null
        );

        // deleted user
        createAndSaveUser(
                "identity4",
                "firstname4",
                "lastname4",
                Gender.M,
                "3333",
                "USR_4",
                "deleted@email.com",
                "username4",
                true,
                OffsetDateTime.now()
        );

        createAndSaveAccountPreferences("identity1", lang1);
        createAndSaveAccountPreferences("identity2", lang2);
        createAndSaveAccountPreferences("identity3", lang2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers_whenCriteriaIsNull() {
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
    void getAllUsers_shouldReturnOnlyOneUser_whenPageSizeIsOne() {
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
    void getAllUsers_shouldReturnTwoUsers_whenPageSizeIsTwo() {
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
    void getAllUsers_shouldReturnCorrectUsers_whenPageNumberRefersToTheSecondPage() {
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
    void getUserByUuid_shouldReturnUser_whenUserExists() {
        when(identityService.getUserById(any()))
                .thenReturn(new IdentityUserResponseDTO());

        UserResponseDTO result = userService.getUserByUuid(enabledUser.getUuid());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(enabledUser.getUuid());
    }

    @Test
    void getUserByUuid_shouldThrowNotFoundException_whenUserNotFound() {
        assertThatThrownBy(() -> userService.getUserByUuid("non-existing-uuid"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'non-existing-uuid' not found");
    }

    @Test
    void createUser_shouldCreateAndReturnDTO_whenTaxIdNumberNotExists() {
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
    void createUser_shouldThrowBadRequestException_whenInvalidLanguageId() {
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
    void updateUser_shouldUpdateAndReturnDTO_whenValidRequest() {
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
    void updateUser_shouldUpdateAndReturnDTO_whenTaxIdNumberChangedAndNotExists() {
        UserRequestDTO dto = buildUserRequestDTO(lang1.getUuid(), "TAX_ID_NEW");

        UserResponseDTO result = userService.updateUser(enabledUser.getUuid(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(enabledUser.getUuid());
        assertThat(result.getTaxIdNumber()).isEqualTo("TAX_ID_NEW");
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenUserNotFound() {
        UserRequestDTO dto = buildUserRequestDTO(lang1.getUuid(), "TAX_ID_NEW");

        assertThatThrownBy(() -> userService.updateUser("non-existing-uuid", dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'non-existing-uuid' not found");
    }

    @Test
    void updateUser_shouldThrowBadRequestException_whenInvalidLanguage() {
        UserRequestDTO dto = buildUserRequestDTO("NON-EXIST-LANG", enabledUser.getTaxIdNumber());

        assertThatThrownBy(() -> userService.updateUser(enabledUser.getUuid(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language with uuid 'NON-EXIST-LANG' not found");
    }

    @Test
    void updateUser_shouldThrowBadRequestException_whenTaxIdNumberAlreadyExists() {
        UserRequestDTO dto = buildUserRequestDTO(lang1.getUuid(), anotherUser.getTaxIdNumber());

        assertThatThrownBy(() -> userService.updateUser(enabledUser.getUuid(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.format("User with taxIdNumber '%s' already exists", anotherUser.getTaxIdNumber()));
    }

    @Test
    void deleteUser_shouldSoftDelete_whenUserExists() {
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
    void deleteUser_shouldThrowNotFoundException_whenUserNotFound() {
        assertThatThrownBy(() -> userService.deleteUser("non-existing-uuid"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'non-existing-uuid' not found");
    }

    private User createAndSaveUser(
            String identityId,
            String firstname,
            String lastname,
            Gender gender,
            String mobileNumber,
            String taxIdNumber,
            String email,
            String username,
            Boolean enabled,
            OffsetDateTime deletedOn
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
        user.setDeletedOn(deletedOn);

        userRepository.save(user);

        return user;
    }

    private Language createAndSaveLanguage(String code, String label, boolean isDefault) {
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

    private void createAndSaveAccountPreferences(
            String identityId,
            Language language
    ) {
        AccountPreferences preferences = new AccountPreferences();
        preferences.setIdentityId(identityId);
        preferences.setLanguage(language);

        accountPreferencesRepository.save(preferences);

    }

}