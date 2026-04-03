package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.mapper.UserMapper;
import com.marcoaga02.modularhub.modules.usermanagement.model.Gender;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import com.marcoaga02.modularhub.modules.usermanagement.repository.UserRepository;
import com.marcoaga02.modularhub.shared.exceptions.BadRequestException;
import com.marcoaga02.modularhub.shared.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user1, user2;

    private UserResponseDTO userResponseDTO1, userResponseDTO2;

    private Language language;

    @BeforeEach
    void setUp() {
        language = new Language();
        language.setCode("it-IT");
        language.setLabel("Italiano");
        language.setIsDefault(true);

        LanguageResponseDTO languageResponseDTO = new LanguageResponseDTO();
        languageResponseDTO.setId(language.getUuid());
        languageResponseDTO.setCode("it-IT");
        languageResponseDTO.setLabel("Italiano");
        languageResponseDTO.setIsDefault(true);

        user1 = new User();
        user1.setFirstname("Mario");
        user1.setLastname("Rossi");
        user1.setGender(Gender.M);
        user1.setLanguage(language);
        user1.setMobileNumber("0000");
        user1.setTaxIdNumber("TAX_ID_01");
        user1.setEmail("email@mock.it");
        user1.setEnabled(true);

        userResponseDTO1 = new UserResponseDTO();
        userResponseDTO1.setId(user1.getUuid());
        userResponseDTO1.setFirstname("Mario");
        userResponseDTO1.setLastname("Rossi");
        userResponseDTO1.setGender(Gender.M.name());
        userResponseDTO1.setLanguage(languageResponseDTO);
        userResponseDTO1.setMobileNumber("0000");
        userResponseDTO1.setTaxIdNumber("TAX_ID_01");
        userResponseDTO1.setEmail("mario@mock.it");
        userResponseDTO1.setEnabled(true);

        user2 = new User();
        user2.setFirstname("Vittoria");
        user2.setLastname("Bianchi");
        user2.setGender(Gender.F);
        user2.setLanguage(language);
        user2.setMobileNumber("1111");
        user2.setTaxIdNumber("TAX_ID_02");
        user2.setEmail("vittoria@mock.it");
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
        userResponseDTO2.setEnabled(false);
    }

    @Test
    void testGetAllUsers_shouldReturnMappedPage() {
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
    void testGetAllUsers_whenNoUsersMatch_shouldReturnEmptyPage() {
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
    void testGetAllUsers_shouldReturnMappedPageWithMultipleUsers() {
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
    void testGetUserByUuid_whenUserExists_shouldReturnDTO() {
        when(userRepository.findByUuid("abc-123"))
                .thenReturn(Optional.of(user1));
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);

        UserResponseDTO result = userService.getUserByUuid("abc-123");

        assertThat(result).isNotNull().isEqualTo(userResponseDTO1);

        verify(userRepository).findByUuid("abc-123");
        verify(userMapper).toDto(user1);
    }

    @Test
    void testGetUserByUuid_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findByUuid("not-existing"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUuid("not-existing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with uuid 'not-existing' not found");

        verify(userRepository).findByUuid("not-existing");
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testCreateUser_whenTaxIdNumberNotExists_shouldCreateAndReturnDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setTaxIdNumber("TAX_ID_01");
        dto.setLanguageId("LANG_ID_01");

        when(languageRepository.findByUuid("LANG_ID_01"))
                .thenReturn(language);
        when(userRepository.findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_01"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        when(userMapper.toDto(user1))
                .thenReturn(userResponseDTO1);

        UserResponseDTO result = userService.createUser(dto);

        assertThat(result).isNotNull().isEqualTo(userResponseDTO1);

        verify(languageRepository).findByUuid("LANG_ID_01");
        verify(userRepository).findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_01");
        verify(userMapper).toDto(user1);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_whenTaxIdNumberAlreadyExists_shouldThrowBadRequestException() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setTaxIdNumber("TAX_ID_01");
        dto.setLanguageId("LANG_ID_01");

        when(languageRepository.findByUuid("LANG_ID_01"))
                .thenReturn(language);
        when(userRepository.findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_01"))
                .thenReturn(Optional.of(user1));

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User with taxIdNumber 'TAX_ID_01' already exists");

        verify(languageRepository).findByUuid("LANG_ID_01");
        verify(userRepository).findByTaxIdNumberAndDeletedOnIsNull("TAX_ID_01");
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testCreateUser_whenInvalidLanguageId_shouldThrowBadRequestException() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setTaxIdNumber("TAX_ID_01");
        dto.setLanguageId("INVALID_LANG");

        when(languageRepository.findByUuid("INVALID_LANG"))
                .thenReturn(null);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language with uuid 'INVALID_LANG' not found");

        verify(languageRepository).findByUuid("INVALID_LANG");
        verify(userRepository, never()).findByTaxIdNumberAndDeletedOnIsNull(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }
}