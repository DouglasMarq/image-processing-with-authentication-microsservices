package com.douglasmarq.imageservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "images_metadata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotNull private UUID userId;

    @NotNull private String imageKey;

    @NotNull private String imageDimension;

    @Column(name = "md5_checksum")
    @NotNull
    private String md5Checksum;

    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;
}
