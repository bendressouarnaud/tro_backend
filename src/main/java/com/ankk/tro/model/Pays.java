package com.ankk.tro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
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
public class Pays {

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
    private String abreviation;

    @OneToMany(fetch = LAZY, mappedBy = "pays")
    private Collection<Utilisateur> utilisateurs;

    @OneToMany(fetch = LAZY, mappedBy = "pays")
    private Collection<Ville> villes;

    @OneToMany(fetch = LAZY, mappedBy = "paysDepart")
    private Collection<Cible> cibleDepart;

    @OneToMany(fetch = LAZY, mappedBy = "paysDestination")
    private Collection<Cible> cibleDest;
}
