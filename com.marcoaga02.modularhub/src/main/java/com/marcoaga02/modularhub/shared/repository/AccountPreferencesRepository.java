package com.marcoaga02.modularhub.shared.repository;

import com.marcoaga02.modularhub.shared.model.AccountPreferences;
import com.marcoaga02.modularhub.shared.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountPreferencesRepository extends JpaRepository<AccountPreferences, Long>, JpaSpecificationExecutor<Language> {

    Optional<AccountPreferences> findByUuid(String uuid);

    Optional<AccountPreferences> findByIdentityId(String identityId);

}
