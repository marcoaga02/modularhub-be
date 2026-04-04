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
import com.marcoaga02.modularhub.shared.exceptions.BadRequestException;
import com.marcoaga02.modularhub.shared.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final LanguageRepository languageRepository;

    private final UserMapper userMapper;

    private final UserSpecificationComposer userSpecComposer;

    public UserService(UserRepository userRepository, LanguageRepository languageRepository, UserMapper userMapper, UserSpecificationComposer userSpecComposer) {
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;
        this.userMapper = userMapper;
        this.userSpecComposer = userSpecComposer;
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

        return userMapper.toDto(user);
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

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String uuid) {
        User user = userRepository.findByUuidAndDeletedOnIsNull(uuid)
                .orElseThrow(() -> new NotFoundException(String.format("User with uuid '%s' not found", uuid)));

        // TODO settare deletedBy quando sarà presente la sessione keycloak
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
