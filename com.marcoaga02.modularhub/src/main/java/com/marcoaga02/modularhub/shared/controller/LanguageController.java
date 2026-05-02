package com.marcoaga02.modularhub.shared.controller;

import com.marcoaga02.modularhub.shared.dto.LanguageResponseDTO;
import com.marcoaga02.modularhub.shared.service.LanguageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/languages")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<List<LanguageResponseDTO>> getLanguages() {
        return ResponseEntity.ok(languageService.getLanguages());
    }
}
