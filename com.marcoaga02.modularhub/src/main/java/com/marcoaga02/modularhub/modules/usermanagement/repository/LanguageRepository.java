package com.marcoaga02.modularhub.modules.usermanagement.repository;

import com.marcoaga02.modularhub.modules.usermanagement.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LanguageRepository extends JpaRepository<Language, Long>, JpaSpecificationExecutor<Language> {
}
