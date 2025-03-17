package com.douglasmarq.auth.domain.repository;

import com.douglasmarq.auth.domain.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository
        extends JpaRepository<Users, UUID>, JpaSpecificationExecutor<Users> {
    Optional<Users> findById(@NonNull UUID id);

    Optional<Users> findByEmail(@NonNull String email);
}
