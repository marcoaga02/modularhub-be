package com.marcoaga02.modularhub.modules.usermanagement.service;

import com.marcoaga02.modularhub.ModularhubApplication;
import com.marcoaga02.modularhub.modules.usermanagement.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import com.marcoaga02.modularhub.modules.usermanagement.repository.LanguageRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ModularhubApplication.class)
@EnableAutoConfiguration
@Transactional
class LanguageServiceTest {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testGetLanguages() {
        Language italianLang = createLanguage("it-IT", "Italiano", true);
        Language englishLang = createLanguage("en-US", "English", false);

        List<LanguageResponseDTO> result = languageService.getLanguages().stream()
                .sorted(Comparator.comparing(LanguageResponseDTO::getCode))
                .toList();

        assertThat(result).hasSize(2);

        assertThat(result.getFirst().getId()).isEqualTo(englishLang.getUuid());
        assertThat(result.getFirst().getCode()).isEqualTo("en-US");
        assertThat(result.getFirst().getLabel()).isEqualTo("English");
        assertThat(result.getFirst().getIsDefault()).isFalse();

        assertThat(result.getLast().getId()).isEqualTo(italianLang.getUuid());
        assertThat(result.getLast().getCode()).isEqualTo("it-IT");
        assertThat(result.getLast().getLabel()).isEqualTo("Italiano");
        assertThat(result.getLast().getIsDefault()).isTrue();
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
