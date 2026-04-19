package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.ModularhubApplication;
import com.marcoaga02.modularhub.config.BaseITClass;
import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ModularhubApplication.class)
@Transactional
class LanguageServiceIT extends BaseITClass {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private EntityManager entityManager;

    private Language italianLang, englishLang;

    @BeforeEach
    void setUp() {
        italianLang = createLanguage("it-IT", "Italiano", true);
        englishLang = createLanguage("en-US", "English", false);
    }

    @Test
    void testGetLanguages() {
        List<LanguageResponseDTO> result = languageService.getLanguages().stream()
                .sorted(Comparator.comparing(LanguageResponseDTO::getCode))
                .toList();

        assertThat(result).hasSize(2);

        assertThat(result).extracting(LanguageResponseDTO::getId)
                .containsExactlyInAnyOrder(italianLang.getUuid(), englishLang.getUuid());
    }

    private Language createLanguage(String code, String label, boolean isDefault) {
        Language language = new Language();
        language.setCode(code);
        language.setLabel(label);
        language.setIsDefault(isDefault);
        languageRepository.save(language);

        entityManager.flush();
        entityManager.clear();

        return language;
    }

}