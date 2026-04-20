package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import com.marcoaga02.modularhub.shared.mapper.LanguageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    private final LanguageMapper languageMapper;

    public LanguageService(LanguageRepository languageRepository, LanguageMapper languageMapper) {
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
    }

    public List<LanguageResponseDTO> getLanguages() {
        return languageRepository.findAll().stream()
                .map(languageMapper::toDto)
                .toList();
    }
}
