package com.douglasmarq.auth.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotNull private String name;

    @NotNull private String email;

    @NotNull private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PlanType plan = PlanType.BASIC;

    @Builder.Default @NotNull private int quotas = 10;

    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
