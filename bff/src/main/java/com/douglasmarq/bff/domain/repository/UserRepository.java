package com.douglasmarq.bff.domain.repository;

import com.douglasmarq.bff.domain.Users;

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
}
