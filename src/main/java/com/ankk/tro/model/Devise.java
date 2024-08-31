package com.ankk.tro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Collection;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
public class Devise {

    @Id
    private Long id;

    @Version
    @Setter
    private int version;

    @CreationTimestamp
    @Setter
    private OffsetDateTime creationDatetime;

    @UpdateTimestamp
    private OffsetDateTime lastUpdateDatetime;

    private String libelle;

    @OneToMany(fetch = LAZY, mappedBy = "devise")
    private Collection<Publication> publications;
}
