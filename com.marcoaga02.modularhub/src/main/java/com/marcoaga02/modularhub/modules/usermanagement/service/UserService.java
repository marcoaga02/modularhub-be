package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.modules.usermanagement.dto.UserCriteriaDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserRequestDTO;
import com.marcoaga02.modularhub.modules.usermanagement.dto.UserResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.mapper.UserMapper;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import com.marcoaga02.modularhub.modules.usermanagement.repository.UserRepository;
import com.marcoaga02.modularhub.modules.usermanagement.specification.UserSpecificationComposer;
import com.marcoaga02.modularhub.shared.dto.IdentityUserCreateRequestDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserResponseDTO;
import com.marcoaga02.modularhub.shared.dto.IdentityUserUpdateRequestDTO;
import com.marcoaga02.modularhub.shared.exception.BadRequestException;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import com.marcoaga02.modularhub.shared.service.CurrentAccountService;
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

    private final UserSpecificationComposer userSpecComposer;

    private final IdentityService identityService;

    private final CurrentAccountService  currentAccountService;

    public UserService(UserRepository userRepository, LanguageRepository languageRepository, UserMapper userMapper, UserSpecificationComposer userSpecComposer, IdentityService identityService, CurrentAccountService currentAccountService) {
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;
        this.userMapper = userMapper;
        this.userSpecComposer = userSpecComposer;
        this.identityService = identityService;
        this.currentAccountService = currentAccountService;
    }

    public Page<UserResponseDTO> getAllUsers(UserCriteriaDTO criteria, Pageable pageable) {
        Page<User> pageResult = userRepository.findAll(
                userSpecComposer.compose(criteria),
                pageable
        );

        return pageResult.map(userMapper::toDto);
    }

    public UserResponseDTO getUserByUuid(String uuid) {
        User user = userRepository.findByUuidAndDeletedOnIsNull(uuid)
                .orElseThrow(() -> new NotFoundException(String.format("User with uuid '%s' not found", uuid)));

        UserResponseDTO dto = userMapper.toDto(user);

        IdentityUserResponseDTO userResponseDTO = identityService.getUserById(user.getIdentityId());

        if (userResponseDTO.getGroups() != null) {
            dto.setGroups(new HashSet<>(userResponseDTO.getGroups()));
        }
        
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
                dto.getGroupsIds()
        );

        String identityId = identityService.createUser(userRequestDTO);
        user.setIdentityId(identityId);

        return userMapper.toDto(userRepository.save(user));
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
                dto.getGroupsIds()
        );

        identityService.updateUser(user.getIdentityId(), userRequestDTO);

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String uuid) {
        User user = userRepository.findByUuidAndDeletedOnIsNull(uuid)
                .orElseThrow(() -> new NotFoundException(String.format("User with uuid '%s' not found", uuid)));

        identityService.deleteUser(user.getIdentityId());

        user.setDeletedBy(currentAccountService.getCurrentAccount().keycloakId());
        user.setDeletedOn(OffsetDateTime.now());
        userRepository.save(user);
    }

    private boolean existsActiveUserWithSameTaxIdNumber(String taxIdNumber) {
        return userRepository.findByTaxIdNumberAndDeletedOnIsNull(taxIdNumber).isPresent();
    }

    private void validateLanguage(String langUuid) {
        Language language = languageRepository.findByUuid(langUuid);

        if (language == null) {
            throw new BadRequestException(String.format("Language with uuid '%s' not found", langUuid));
        }
    }

}
