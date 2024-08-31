package com.ankk.tro.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Long id;

    @Version
    @Setter
    private int version;

    @CreationTimestamp
    @Setter
    private OffsetDateTime creationDatetime;

    @UpdateTimestamp
    private OffsetDateTime lastUpdateDatetime;

}
