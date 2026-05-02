package com.marcoaga02.modularhub.shared.repository;

import com.marcoaga02.modularhub.shared.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long>, JpaSpecificationExecutor<Language> {

    Optional<Language> findByUuid(String uuid);

}
