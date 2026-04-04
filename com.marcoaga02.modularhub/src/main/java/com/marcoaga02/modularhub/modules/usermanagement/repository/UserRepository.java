package com.marcoaga02.modularhub.modules.usermanagement.repository;

import com.marcoaga02.modularhub.modules.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUuidAndDeletedOnIsNull(String uuid);

    Optional<User> findByTaxIdNumberAndDeletedOnIsNull(String taxIdNumber);
}