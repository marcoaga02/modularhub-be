package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.mapper.UserMapper;
import com.marcoaga02.modularhub.modules.usermanagement.model.Gender;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.repository.UserRepository;
import com.marcoaga02.modularhub.shared.dto.*;
import com.marcoaga02.modularhub.shared.exception.BadRequestException;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import com.marcoaga02.modularhub.shared.model.Language;
import com.marcoaga02.modularhub.shared.repository.LanguageRepository;
import com.marcoaga02.modularhub.shared.service.AccountPreferencesService;
import com.marcoaga02.modularhub.shared.service.AccountService;
import com.marcoaga02.modularhub.shared.service.IdentityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final OffsetDateTime CREATED_ON = OffsetDateTime.parse("2026-01-01T10:15:30+01:00");
    private static final OffsetDateTime UPDATED_ON = OffsetDateTime.parse("2026-02-01T10:15:30+01:00");

    @Mock
    private UserRepository userRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private IdentityService identityService;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountPreferencesService accountPreferencesService;

    @InjectMocks
    private UserService userService;

    private User user1, user2;

    private UserResponseDTO userResponseDTO1, userResponseDTO2;

    private Language language;

    private LanguageResponseDTO languageResponseDTO;

    @BeforeEach
    void setUp() {
        language = new Language();
        language.setCode("it-IT");
        language.setLabel("Italiano");
        language.setIsDefault(true);

        languageResponseDTO = new LanguageResponseDTO();
        languageResponseDTO.setId(language.getUuid());
        languageResponseDTO.setCode("it-IT");
        languageResponseDTO.setLabel("Italiano");
        languageResponseDTO.setIsDefault(true);

        user1 = new User();
        user1.setFirstname("Mario");
        user1.setLastname("Rossi");
        user1.setGender(Gender.M);
        user1.setMobileNumber("0000");
        user1.setTaxIdNumber("TAX_ID_01");
        user1.setEmail("email@mock.it");
        user1.setUsername("username1");
        user1.setEnabled(true);
        user1.setCreatedOn(CREATED_ON);
        user1.setUpdatedOn(UPDATED_ON);
        user1.setCreatedBy("Vittoria Bianchi");
        user1.setUpdatedBy("Mario Rossi");

        userResponseDTO1 = new UserResponseDTO();
        userResponseDTO1.setId(user1.getUuid());
        userResponseDTO1.setFirstname("Mario");
        userResponseDTO1.setLastname("Rossi");
        userResponseDTO1.setGender(Gender.M.name());
        userResponseDTO1.setLanguage(languageResponseDTO);
        userResponseDTO1.setMobileNumber("0000");
        userResponseDTO1.setTaxIdNumber("TAX_ID_01");
        userResponseDTO1.setEmail("mario@mock.it");
        userResponseDTO1.setUsername("username1");
        userResponseDTO1.setEnabled(true);

        user2 = new User();
        user2.setFirstname("Vittoria");
        user2.setLastname("Bianchi");
        user2.setGender(Gender.F);
        user2.setMobileNumber("1111");
        user2.setTaxIdNumber("TAX_ID_02");
        user2.setEmail("vittoria@mock.it");
        user2.setUsername("username2");
        user2.setEnabled(false);

        userResponseDTO2 = new UserResponseDTO();
        userResponseDTO2.setId(user1.getUuid());
        userResponseDTO2.setFirstname("Vittoria");
        userResponseDTO2.setLastname("Bianci");
        userResponseDTO2.setGender(Gender.F.name());
        userResponseDTO2.setLanguage(languageResponseDTO);
        userResponseDTO2.setMobileNumber("1111");
        userResponseDTO2.setTaxIdNumber("TAX_ID_02");
        userResponseDTO2.setEmail("vittoria@mock.it");
        userResponseDTO2.setUsername("username2");
        userResponseDTO2.setEnabled(false);
    }

    @Test
    void getAllUsers_shouldReturnEmptyPage_whenNoUsersMatch() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = Page.empty(pageable);

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable)))
                .thenReturn(emptyPage);

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, pageable);

        assertThat(result).isEmpty();

        verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable));
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void getAllUsers_shouldReturnMappedPage_whenSingleUser() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user1), pageable, 1);

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable)))
                .thenReturn(userPage);
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).containsExactly(userResponseDTO1);

        verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable));
        verify(userMapper).toDto(user1);
    }

    @Test
    void getAllUsers_shouldReturnMappedPage_whenMultipleUsers() {
        UserCriteriaDTO criteria = new UserCriteriaDTO();
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable)))
                .thenReturn(userPage);
        when(userMapper.toDto(user1)).thenReturn(userResponseDTO1);
        when(userMapper.toDto(user2)).thenReturn(userResponseDTO2);

        Page<UserResponseDTO> result = userService.getAllUsers(criteria, pageable);

        assertThat(result.getContent()).containsExactly(userResponseDTO1, userResponseDTO2);

        verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable));
        verify(userMapper).toDto(user1);
        verify(userMapper).toDto(user2);
    }

    @Test
    void getUserByUuid_shouldReturnDTO_whenUserExists() {
        final String userUuid = "abc-123";
        final String identityId1 = "identityId1";
        final String identityId2 = "identityId2";

        user1.setIdentityId(identityId1);
        user1.setCreatedBy(identityId2);
        user1.setUpdatedBy(identityId1);

        IdentityGroupDTO groupDTO = new IdentityGroupDTO();
        groupDTO.setId("group-id");
        groupDTO.setName("First Group");
        groupDTO.setDescription("First Group Description");

        IdentityUserResponseDTO userResponseDTO = new IdentityUserResponseDTO();
        userResponseDTO.setId(identityId1);
        userResponseDTO.setUsername("username");
        userResponseDTO.setEmail("email");
        userResponseDTO.setFirstName("firstName");
        userResponseDTO.setLastName("lastName");
        userResponseDTO.setEnabled(true);
        userResponseDTO.setGroups(List.of(groupDTO));

        AccountPreferencesResponseDTO preferencesResponseDTO = new AccountPreferencesResponseDTO();
        preferencesResponseDTO.setLanguage(languageResponseDTO);

        when(userRepository.findByUuidAndDeletedOnIsNull(userUuid))
                .thenReturn(Optional.of(user1));
        when(userRepository.findByIdentityId(identityId2))
                .thenReturn(Optional.of(user2));
        when(userRepository.findByIdentityId(identityId1))
                .thenReturn(Optional.of(user1));
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);
        when(identityService.getUserById(identityId1))
                .thenReturn(userResponseDTO);
        when(accountPreferencesService.getAccountPreferences(identityId1))
                .thenReturn(preferencesResponseDTO);

        UserResponseDTO result = userService.getUserByUuid(userUuid);

        assertThat(result).isNotNull().isEqualTo(userResponseDTO1);
        assertThat(result.getAudit()).isNotNull();
        assertThat(result.getAudit().getCreatedBy()).isEqualTo("Vittoria Bianchi");
        assertThat(result.getAudit().getUpdatedBy()).isEqualTo("Mario Rossi");
        assertThat(result.getAudit().getCreatedOn()).isEqualTo(CREATED_ON);
        assertThat(result.getAudit().getUpdatedOn()).isEqualTo(UPDATED_ON);

        assertThat(result.getGroups()).hasSize(1).containsExactly(groupDTO);
        assertThat(result.getLanguage()).isEqualTo(languageResponseDTO);

        verify(userRepository).findByUuidAndDeletedOnIsNull(userUuid);
        verify(userRepository).findByIdentityId(identityId2);
        verify(userRepository).findByIdentityId(identityId1);
        verify(identityService).getUserById(identityId1);
        verify(accountPreferencesService).getAccountPreferences(identityId1);
        verify(userMapper).toDto(user1);
    }

    @Test
    void getUserByUuid_shouldReturnDTO_whenUserExistsWithNullGroups() {
        final String userUuid = "abc-123";
        final String identityId1 = "identityId1";
        final String identityId2 = "identityId2";

        user1.setIdentityId(identityId1);
        user1.setCreatedBy(identityId2);
        user1.setUpdatedBy(identityId1);

        IdentityGroupDTO groupDTO = new IdentityGroupDTO();
        groupDTO.setId("group-id");
        groupDTO.setName("First Group");
        groupDTO.setDescription("First Group Description");

        IdentityUserResponseDTO userResponseDTO = new IdentityUserResponseDTO();
        userResponseDTO.setId(identityId1);
        userResponseDTO.setUsername("username");
        userResponseDTO.setEmail("email");
        userResponseDTO.setFirstName("firstName");
        userResponseDTO.setLastName("lastName");
        userResponseDTO.setEnabled(true);
        userResponseDTO.setGroups(null);

        AccountPreferencesResponseDTO preferencesResponseDTO = new AccountPreferencesResponseDTO();
        preferencesResponseDTO.setLanguage(languageResponseDTO);

        when(userRepository.findByUuidAndDeletedOnIsNull(userUuid))
                .thenReturn(Optional.of(user1));
        when(userRepository.findByIdentityId(identityId2))
                .thenReturn(Optional.of(user2));
        when(userRepository.findByIdentityId(identityId1))
                .thenReturn(Optional.of(user1));
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);
        when(identityService.getUserById(identityId1))
                .thenReturn(userResponseDTO);
        when(accountPreferencesService.getAccountPreferences(identityId1))
                .thenReturn(preferencesResponseDTO);

        UserResponseDTO result = userService.getUserByUuid(userUuid);

        assertThat(result).isNotNull().isEqualTo(userResponseDTO1);
        assertThat(result.getAudit()).isNotNull();
        assertThat(result.getAudit().getCreatedBy()).isEqualTo("Vittoria Bianchi");
        assertThat(result.getAudit().getUpdatedBy()).isEqualTo("Mario Rossi");
        assertThat(result.getAudit().getCreatedOn()).isEqualTo(CREATED_ON);
        assertThat(result.getAudit().getUpdatedOn()).isEqualTo(UPDATED_ON);

        assertThat(result.getGroups()).isEmpty();

        verify(userRepository).findByUuidAndDeletedOnIsNull(userUuid);
        verify(userRepository).findByIdentityId(identityId2);
        verify(userRepository).findByIdentityId(identityId1);
        verify(identityService).getUserById(identityId1);
        verify(accountPreferencesService).getAccountPreferences(identityId1);
        verify(userMapper).toDto(user1);
    }

    @Test
    void getUserByUuid_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findByUuidAndDeletedOnIsNull("not-existing"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUuid("not-existing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'not-existing' not found");

        verify(userRepository).findByUuidAndDeletedOnIsNull("not-existing");
        verify(userRepository, never()).findByIdentityId(anyString());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void createUser_shouldCreateAndReturnDTO_whenTaxIdNumberNotExists() {
        final String identityId = "identityId";

        UserRequestDTO dto = buildUserRequestDTO();

        AccountPreferencesResponseDTO preferencesResponseDTO = new AccountPreferencesResponseDTO();
        preferencesResponseDTO.setLanguage(languageResponseDTO);

        when(languageRepository.findByUuid("LANG_ID_01"))
                .thenReturn(Optional.of(language));
        when(userRepository.findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_NEW"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);
        when(identityService.createUser(any(IdentityUserCreateRequestDTO.class)))
                .thenReturn(identityId);
        when(accountPreferencesService.createAccountPreferences(any(AccountPreferencesCreateDTO.class)))
                .thenReturn(preferencesResponseDTO);

        UserResponseDTO result = userService.createUser(dto);

        assertThat(result).isNotNull().isEqualTo(userResponseDTO1);

        verify(languageRepository).findByUuid("LANG_ID_01");
        verify(userRepository).findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_NEW");

        ArgumentCaptor<IdentityUserCreateRequestDTO> identityCaptor =
                ArgumentCaptor.forClass(IdentityUserCreateRequestDTO.class);

        verify(identityService).createUser(identityCaptor.capture());

        IdentityUserCreateRequestDTO captured = identityCaptor.getValue();

        assertThat(captured.getUsername()).isEqualTo("new username");
        assertThat(captured.getEmail()).isEqualTo("new@email.com");
        assertThat(captured.getFirstName()).isEqualTo("new firstname");
        assertThat(captured.getLastName()).isEqualTo("new lastname");
        assertThat(captured.getEnabled()).isFalse();
        assertThat(captured.getPassword()).isEqualTo("password");
        assertThat(captured.getGroupIds()).containsExactly("group-id");
        assertThat(captured.getEmailVerified()).isTrue();

        ArgumentCaptor<AccountPreferencesCreateDTO> preferencesCaptor =
                ArgumentCaptor.forClass(AccountPreferencesCreateDTO.class);

        verify(accountPreferencesService).createAccountPreferences(preferencesCaptor.capture());

        AccountPreferencesCreateDTO preferencesCaptured = preferencesCaptor.getValue();

        assertThat(preferencesCaptured.getIdentityId()).isEqualTo(identityId);
        assertThat(preferencesCaptured.getLanguageId()).isEqualTo("LANG_ID_01");

        verify(userMapper).toDto(user1);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getIdentityId()).isEqualTo(identityId);
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenTaxIdNumberAlreadyExists() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setTaxIdNumber("TAX_ID_01");
        dto.setLanguageId("LANG_ID_01");

        when(languageRepository.findByUuid("LANG_ID_01"))
                .thenReturn(Optional.of(language));
        when(userRepository.findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_01"))
                .thenReturn(Optional.of(user1));

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User with taxIdNumber 'TAX_ID_01' already exists");

        verify(languageRepository).findByUuid("LANG_ID_01");
        verify(userRepository).findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_01");
        verify(identityService, never()).createUser(any(IdentityUserCreateRequestDTO.class));
        verify(accountPreferencesService, never()).createAccountPreferences(any(AccountPreferencesCreateDTO.class));
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenInvalidLanguageId() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setTaxIdNumber("TAX_ID_01");
        dto.setLanguageId("INVALID_LANG");

        when(languageRepository.findByUuid("INVALID_LANG"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language with uuid 'INVALID_LANG' not found");

        verify(languageRepository).findByUuid("INVALID_LANG");
        verify(userRepository, never()).findByTaxIdNumberAndDeletedOnIsNull(any());
        verify(identityService, never()).createUser(any(IdentityUserCreateRequestDTO.class));
        verify(accountPreferencesService, never()).createAccountPreferences(any(AccountPreferencesCreateDTO.class));
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void updateUser_shouldUpdateAndReturnDTO_whenValid() {
        final String identityId = "identityId";
        user1.setIdentityId(identityId);

        UserRequestDTO dto = buildUserRequestDTO();
        dto.setTaxIdNumber(user1.getTaxIdNumber());

        AccountPreferencesResponseDTO preferencesResponseDTO = new AccountPreferencesResponseDTO();
        preferencesResponseDTO.setLanguage(languageResponseDTO);

        when(userRepository.findByUuidAndDeletedOnIsNull(user1.getUuid()))
                .thenReturn(Optional.of(user1));
        when(languageRepository.findByUuid("LANG_ID_01"))
                .thenReturn(Optional.of(language));
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);
        when(accountPreferencesService.updateAccountPreferencesByIdentityId(
                eq(identityId),
                any(AccountPreferencesUpdateDTO.class)
        )).thenReturn(preferencesResponseDTO);

        UserResponseDTO result = userService.updateUser(user1.getUuid(), dto);

        assertThat(result).isNotNull().isEqualTo(userResponseDTO1);

        verify(userRepository).findByUuidAndDeletedOnIsNull(user1.getUuid());
        verify(languageRepository).findByUuid("LANG_ID_01");
        verify(userRepository, never()).findByTaxIdNumberAndDeletedOnIsNull(any());
        verify(userMapper).updateEntity(dto, user1);

        ArgumentCaptor<IdentityUserUpdateRequestDTO> captor =
                ArgumentCaptor.forClass(IdentityUserUpdateRequestDTO.class);

        verify(identityService).updateUser(eq(identityId), captor.capture());

        IdentityUserUpdateRequestDTO captured = captor.getValue();

        assertThat(captured.getUsername()).isEqualTo("new username");
        assertThat(captured.getEmail()).isEqualTo("new@email.com");
        assertThat(captured.getFirstName()).isEqualTo("new firstname");
        assertThat(captured.getLastName()).isEqualTo("new lastname");
        assertThat(captured.getEnabled()).isFalse();
        assertThat(captured.getGroupIds()).containsExactly("group-id");

        ArgumentCaptor<AccountPreferencesUpdateDTO> preferencesCaptor =
                ArgumentCaptor.forClass(AccountPreferencesUpdateDTO.class);

        verify(accountPreferencesService).updateAccountPreferencesByIdentityId(
                eq(identityId),
                preferencesCaptor.capture()
        );

        AccountPreferencesUpdateDTO preferencesCaptured = preferencesCaptor.getValue();

        assertThat(preferencesCaptured.getLanguageId()).isEqualTo("LANG_ID_01");

        verify(userMapper).toDto(user1);
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenUserNotFound() {
        UserRequestDTO dto = buildUserRequestDTO();

        when(userRepository.findByUuidAndDeletedOnIsNull("not-existing"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser("not-existing", dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'not-existing' not found");

        verify(userRepository).findByUuidAndDeletedOnIsNull("not-existing");
        verify(languageRepository, never()).findByUuid(any());
        verify(userRepository, never()).findByTaxIdNumberAndDeletedOnIsNull(any());
        verify(userMapper, never()).updateEntity(any(), any());
        verify(identityService, never()).updateUser(any(), any());
        verify(accountPreferencesService, never()).updateAccountPreferencesByIdentityId(anyString(), any(AccountPreferencesUpdateDTO.class));
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void updateUser_shouldThrowBadRequestException_whenInvalidLanguage() {
        UserRequestDTO dto = buildUserRequestDTO();
        dto.setLanguageId("INVALID_LANG");

        when(userRepository.findByUuidAndDeletedOnIsNull(user1.getUuid()))
                .thenReturn(Optional.of(user1));
        when(languageRepository.findByUuid("INVALID_LANG"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(user1.getUuid(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language with uuid 'INVALID_LANG' not found");

        verify(userRepository).findByUuidAndDeletedOnIsNull(user1.getUuid());
        verify(languageRepository).findByUuid("INVALID_LANG");
        verify(userRepository, never()).findByTaxIdNumberAndDeletedOnIsNull(any());
        verify(userMapper, never()).updateEntity(any(), any());
        verify(identityService, never()).updateUser(any(), any());
        verify(accountPreferencesService, never()).updateAccountPreferencesByIdentityId(anyString(), any(AccountPreferencesUpdateDTO.class));
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void updateUser_shouldThrowBadRequestException_whenTaxIdNumberAlreadyExists() {
        UserRequestDTO dto = buildUserRequestDTO();
        dto.setTaxIdNumber("TAX_ID_02");

        when(userRepository.findByUuidAndDeletedOnIsNull(user1.getUuid()))
                .thenReturn(Optional.of(user1));
        when(languageRepository.findByUuid("LANG_ID_01"))
                .thenReturn(Optional.of(language));
        when(userRepository.findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_02"))
                .thenReturn(Optional.of(user2));

        assertThatThrownBy(() -> userService.updateUser(user1.getUuid(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User with taxIdNumber 'TAX_ID_02' already exists");

        verify(userRepository).findByUuidAndDeletedOnIsNull(user1.getUuid());
        verify(languageRepository).findByUuid("LANG_ID_01");
        verify(userRepository).findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_02");
        verify(userMapper, never()).updateEntity(any(), any());
        verify(identityService, never()).updateUser(any(), any());
        verify(accountPreferencesService, never()).updateAccountPreferencesByIdentityId(anyString(), any(AccountPreferencesUpdateDTO.class));
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void updateUser_shouldUpdateAndReturnDTO_whenTaxIdNumberChangedAndNotExists() {
        final String identityId = "identityId";
        user1.setIdentityId(identityId);

        UserRequestDTO dto = buildUserRequestDTO();
        dto.setTaxIdNumber("TAX_ID_NEW");

        AccountPreferencesResponseDTO preferencesResponseDTO = new AccountPreferencesResponseDTO();
        preferencesResponseDTO.setLanguage(languageResponseDTO);

        when(userRepository.findByUuidAndDeletedOnIsNull(user1.getUuid()))
                .thenReturn(Optional.of(user1));
        when(languageRepository.findByUuid("LANG_ID_01"))
                .thenReturn(Optional.of(language));
        when(userRepository.findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_NEW"))
                .thenReturn(Optional.empty());
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);
        when(accountPreferencesService.updateAccountPreferencesByIdentityId(
                eq(identityId),
                any(AccountPreferencesUpdateDTO.class)
        )).thenReturn(preferencesResponseDTO);

        UserResponseDTO result = userService.updateUser(user1.getUuid(), dto);

        assertThat(result).isNotNull().isEqualTo(userResponseDTO1);

        verify(userRepository).findByUuidAndDeletedOnIsNull(user1.getUuid());
        verify(languageRepository).findByUuid("LANG_ID_01");
        verify(userRepository).findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_NEW");
        verify(userMapper).updateEntity(dto, user1);

        ArgumentCaptor<IdentityUserUpdateRequestDTO> captor =
                ArgumentCaptor.forClass(IdentityUserUpdateRequestDTO.class);

        verify(identityService).updateUser(eq(identityId), captor.capture());

        IdentityUserUpdateRequestDTO captured = captor.getValue();

        assertThat(captured.getUsername()).isEqualTo("new username");
        assertThat(captured.getEmail()).isEqualTo("new@email.com");
        assertThat(captured.getFirstName()).isEqualTo("new firstname");
        assertThat(captured.getLastName()).isEqualTo("new lastname");
        assertThat(captured.getEnabled()).isFalse();
        assertThat(captured.getGroupIds()).containsExactly("group-id");

        ArgumentCaptor<AccountPreferencesUpdateDTO> preferencesCaptor =
                ArgumentCaptor.forClass(AccountPreferencesUpdateDTO.class);

        verify(accountPreferencesService).updateAccountPreferencesByIdentityId(
                eq(identityId),
                preferencesCaptor.capture()
        );

        AccountPreferencesUpdateDTO preferencesCaptured = preferencesCaptor.getValue();

        assertThat(preferencesCaptured.getLanguageId()).isEqualTo("LANG_ID_01");

        verify(userMapper).toDto(user1);
    }

    @Test
    void deleteUser_shouldSoftDeleteUser_whenValid() {
        AccountDTO currentAccount = new AccountDTO(
                "identityId",
                "user@email.com",
                "username",
                "firstName",
                "lastName",
                Set.of(),
                new AccountPreferencesResponseDTO()
        );

        when(userRepository.findByUuidAndDeletedOnIsNull(user1.getUuid()))
                .thenReturn(Optional.of(user1));
        when(accountService.getCurrentAccount())
                .thenReturn(currentAccount);

        assertThat(user1.getDeletedOn()).isNull();

        userService.deleteUser(user1.getUuid());

        assertThat(user1.getDeletedBy()).isEqualTo("identityId");
        assertThat(user1.getDeletedOn()).isNotNull();

        verify(userRepository).findByUuidAndDeletedOnIsNull(user1.getUuid());
        verify(accountService).getCurrentAccount();
        verify(userRepository).save(user1);
    }

    @Test
    void deleteUser_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findByUuidAndDeletedOnIsNull("not-existing"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser("not-existing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'not-existing' not found");

        verify(userRepository).findByUuidAndDeletedOnIsNull("not-existing");
        verify(accountService, never()).getCurrentAccount();
        verify(userRepository, never()).save(any());
    }

    private UserRequestDTO buildUserRequestDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstname("new firstname");
        dto.setLastname("new lastname");
        dto.setGender(Gender.F);
        dto.setLanguageId("LANG_ID_01");
        dto.setMobileNumber("9999");
        dto.setTaxIdNumber("TAX_ID_NEW");
        dto.setEmail("new@email.com");
        dto.setUsername("new username");
        dto.setEnabled(false);
        dto.setPassword("password");
        dto.setGroupIds(List.of("group-id"));
        return dto;
    }

}