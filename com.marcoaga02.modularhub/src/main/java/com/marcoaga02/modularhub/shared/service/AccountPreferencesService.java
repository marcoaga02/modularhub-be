package com.marcoaga02.modularhub.shared.service;

import com.marcoaga02.modularhub.shared.dto.AccountPreferencesCreateDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesResponseDTO;
import com.marcoaga02.modularhub.shared.dto.AccountPreferencesUpdateDTO;
import com.marcoaga02.modularhub.shared.exception.NotFoundException;
import com.marcoaga02.modularhub.shared.mapper.AccountPreferencesMapper;
import com.marcoaga02.modularhub.shared.model.AccountPreferences;
import com.marcoaga02.modularhub.shared.repository.AccountPreferencesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO testare

@Service
public class AccountPreferencesService {

    private final AccountPreferencesRepository accountPreferencesRepository;

    private final AccountPreferencesMapper accountPreferencesMapper;

    public AccountPreferencesService(AccountPreferencesRepository accountPreferencesRepository, AccountPreferencesMapper accountPreferencesMapper) {
        this.accountPreferencesRepository = accountPreferencesRepository;
        this.accountPreferencesMapper = accountPreferencesMapper;
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
        AccountPreferences accountPreferences = new AccountPreferences();

        accountPreferencesMapper.toEntity(dto, accountPreferences);

        return accountPreferencesMapper.toDto(accountPreferencesRepository.save(accountPreferences));
    }

    @Transactional
    public AccountPreferencesResponseDTO updateAccountPreferencesByIdentityId(String identityId, AccountPreferencesUpdateDTO dto) {
        AccountPreferences accountPreferences = accountPreferencesRepository.findByIdentityId(identityId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Account preferences for identityId %s not found", identityId)
                        )
                );

        accountPreferencesMapper.updateEntity(dto, accountPreferences);

        return accountPreferencesMapper.toDto(accountPreferencesRepository.save(accountPreferences));
    }

    // TODO implementare la delete

}
