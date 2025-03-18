package com.douglasmarq.imageservice.domain.repository;

import com.douglasmarq.imageservice.domain.Images;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImagesRepository
        extends JpaRepository<Images, UUID>, JpaSpecificationExecutor<Images> {
    Optional<Images> findById(@NotNull UUID id);

    List<Images> findAllByMd5Checksum(@NotNull String checksum);

    List<Images> findAllByUserId(@NotNull UUID userId);
}
