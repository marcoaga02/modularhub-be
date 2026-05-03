package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.mapper.UserMapper;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.repository.UserRepository;
import com.marcoaga02.modularhub.modules.usermanagement.specification.UserSpecification;
import com.marcoaga02.modularhub.shared.dto.*;
import com.marcoaga02.modularhub.shared.exception.BadRequestException;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import com.marcoaga02.modularhub.shared.repository.LanguageRepository;
import com.marcoaga02.modularhub.shared.service.AccountPreferencesService;
import com.marcoaga02.modularhub.shared.service.AccountService;
import com.marcoaga02.modularhub.shared.service.IdentityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;

@Service
public class UserService {

    private static final Boolean EMAIL_VERIFIED = true;

    private final UserRepository userRepository;

    private final LanguageRepository languageRepository;

    private final UserMapper userMapper;

    private final IdentityService identityService;

    private final AccountService accountService;

    private final AccountPreferencesService accountPreferencesService;

    public UserService(UserRepository userRepository, LanguageRepository languageRepository, UserMapper userMapper, IdentityService identityService, AccountService accountService, AccountPreferencesService accountPreferencesService) {
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;
        this.userMapper = userMapper;
        this.identityService = identityService;
        this.accountService = accountService;
        this.accountPreferencesService = accountPreferencesService;
    }

    public Page<UserResponseDTO> getAllUsers(UserCriteriaDTO criteria, Pageable pageable) {
        Page<User> pageResult = userRepository.findAll(
                UserSpecification.byCriteria(criteria),
                pageable
        );

        return pageResult.map(userMapper::toDto);
    }

    public UserResponseDTO getUserByUuid(String uuid) {
        User user = userRepository.findByUuidAndDeletedOnIsNull(uuid)
                .orElseThrow(() -> new NotFoundException(String.format("User with uuid '%s' not found", uuid)));

        AuditDTO auditDto = new AuditDTO();
        auditDto.setCreatedOn(user.getCreatedOn());
        auditDto.setCreatedBy(resolveUserFullName(user.getCreatedBy()));
        auditDto.setUpdatedOn(user.getUpdatedOn());
        auditDto.setUpdatedBy(resolveUserFullName(user.getUpdatedBy()));

        UserResponseDTO dto = userMapper.toDto(user);
        dto.setAudit(auditDto);

        IdentityUserResponseDTO userResponseDTO = identityService.getUserById(user.getIdentityId());

        if (userResponseDTO.getGroups() != null) {
            dto.setGroups(new HashSet<>(userResponseDTO.getGroups()));
        }

        AccountPreferencesResponseDTO preferencesDTO = accountPreferencesService.getAccountPreferences(user.getIdentityId());

        dto.setLanguage(preferencesDTO.getLanguage());
        
        return dto;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        validateLanguage(dto.getLanguageId());

        if (existsActiveUserWithSameTaxIdNumber(dto.getTaxIdNumber())) {
            throw new BadRequestException(
                    String.format("User with taxIdNumber '%s' already exists", dto.getTaxIdNumber())
            );
        }

        User user = new User();

        userMapper.updateEntity(dto, user);

        IdentityUserCreateRequestDTO userRequestDTO = new IdentityUserCreateRequestDTO(
                dto.getUsername(),
                dto.getEmail(),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getEnabled(),
                EMAIL_VERIFIED,
                dto.getPassword(),
                dto.getGroupIds()
        );

        String identityId = identityService.createUser(userRequestDTO);
        user.setIdentityId(identityId);

        AccountPreferencesCreateDTO preferencesRequestDTO = new AccountPreferencesCreateDTO(
                identityId,
                dto.getLanguageId()
        );

        AccountPreferencesResponseDTO preferencesResponseDTO =
                accountPreferencesService.createAccountPreferences(preferencesRequestDTO);

        UserResponseDTO userResponseDTO = userMapper.toDto(userRepository.save(user));
        userResponseDTO.setLanguage(preferencesResponseDTO.getLanguage());

        return userResponseDTO;
    }

    @Transactional
    public UserResponseDTO updateUser(String uuid, UserRequestDTO dto) {
        User user = userRepository.findByUuidAndDeletedOnIsNull(uuid)
                .orElseThrow(() -> new NotFoundException(String.format("User with uuid '%s' not found", uuid)));

        validateLanguage(dto.getLanguageId());

        if (!Objects.equals(user.getTaxIdNumber(), dto.getTaxIdNumber())
                && existsActiveUserWithSameTaxIdNumber(dto.getTaxIdNumber())
        ) {
            throw new BadRequestException(
                    String.format("User with taxIdNumber '%s' already exists", dto.getTaxIdNumber())
            );
        }

        userMapper.updateEntity(dto, user);

        IdentityUserUpdateRequestDTO userRequestDTO = new IdentityUserUpdateRequestDTO(
                dto.getUsername(),
                dto.getEmail(),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getEnabled(),
                dto.getGroupIds()
        );

        identityService.updateUser(user.getIdentityId(), userRequestDTO);

        AccountPreferencesUpdateDTO preferencesRequestDTO = new AccountPreferencesUpdateDTO(
                dto.getLanguageId()
        );

        AccountPreferencesResponseDTO preferencesResponseDTO =
                accountPreferencesService.updateAccountPreferencesByIdentityId(user.getIdentityId(), preferencesRequestDTO);

        UserResponseDTO userResponseDTO = userMapper.toDto(user);
        userResponseDTO.setLanguage(preferencesResponseDTO.getLanguage());

        return userResponseDTO;
    }

    @Transactional
    public void deleteUser(String uuid) {
        User user = userRepository.findByUuidAndDeletedOnIsNull(uuid)
                .orElseThrow(() -> new NotFoundException(String.format("User with uuid '%s' not found", uuid)));

        identityService.deleteUser(user.getIdentityId());

        user.setDeletedBy(accountService.getCurrentAccount().getIdentityId());
        user.setDeletedOn(OffsetDateTime.now());
        userRepository.save(user);
    }

    private boolean existsActiveUserWithSameTaxIdNumber(String taxIdNumber) {
        return userRepository.findByTaxIdNumberAndDeletedOnIsNull(taxIdNumber).isPresent();
    }

    private void validateLanguage(String langUuid) {
        languageRepository.findByUuid(langUuid)
                .orElseThrow(() -> new BadRequestException(String.format("Language with uuid '%s' not found", langUuid)));
    }

    private String resolveUserFullName(String identityId) {
        if (identityId == null) {
            return "";
        }

        return userRepository.findByIdentityId(identityId)
                .map(User::getFullName)
                .orElse("");
    }

}
