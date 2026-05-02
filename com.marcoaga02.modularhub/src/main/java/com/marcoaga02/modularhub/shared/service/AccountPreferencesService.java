package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.dto.AccountPreferencesCreateDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesUpdateDTO;
import com.marcoaga02.modularhub.shared.exception.BadRequestException;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import com.marcoaga02.modularhub.shared.mapper.AccountPreferencesMapper;
import com.marcoaga02.modularhub.shared.model.AccountPreferences;
import com.marcoaga02.modularhub.shared.model.Language;
import com.marcoaga02.modularhub.shared.repository.AccountPreferencesRepository;
import com.marcoaga02.modularhub.shared.repository.LanguageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountPreferencesService {

    private final AccountPreferencesRepository accountPreferencesRepository;

    private final AccountPreferencesMapper accountPreferencesMapper;

    private final LanguageRepository languageRepository;

    public AccountPreferencesService(AccountPreferencesRepository accountPreferencesRepository, AccountPreferencesMapper accountPreferencesMapper, LanguageRepository languageRepository) {
        this.accountPreferencesRepository = accountPreferencesRepository;
        this.accountPreferencesMapper = accountPreferencesMapper;
        this.languageRepository = languageRepository;
    }

    public AccountPreferencesResponseDTO getAccountPreferences(String identityId) {
        if (identityId == null) {
            throw new IllegalArgumentException("identityId cannot be null");
        }

        AccountPreferences accountPreferences = accountPreferencesRepository.findByIdentityId(identityId)
                .orElseThrow(() -> new NotFoundException(String.format("AccountPreferences for identityId '%s' not found", identityId)));

        return accountPreferencesMapper.toDto(accountPreferences);
    }

    @Transactional
    public AccountPreferencesResponseDTO createAccountPreferences(AccountPreferencesCreateDTO dto) {
        Language language = validateLanguageOrElseThrow(dto.getLanguageId());

        AccountPreferences preferences = new AccountPreferences();
        preferences.setIdentityId(dto.getIdentityId());
        preferences.setLanguage(language);

        return accountPreferencesMapper.toDto(accountPreferencesRepository.save(preferences));
    }

    @Transactional
    public AccountPreferencesResponseDTO updateAccountPreferencesByIdentityId(String identityId, AccountPreferencesUpdateDTO dto) {
        AccountPreferences accountPreferences = accountPreferencesRepository.findByIdentityId(identityId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("AccountPreferences for identityId '%s' not found", identityId)
                        )
                );

        Language language = validateLanguageOrElseThrow(dto.getLanguageId());
        accountPreferences.setLanguage(language);

        return accountPreferencesMapper.toDto(accountPreferences);
    }

    private Language validateLanguageOrElseThrow(String languageId) {
        return languageRepository.findByUuid(languageId)
                .orElseThrow(() -> new NotFoundException(String.format("Language with uuid '%s' not found", languageId)));
    }

}
