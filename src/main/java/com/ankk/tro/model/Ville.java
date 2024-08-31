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
@Table(
        indexes = {
                @Index(name = "pays_ville_id_idx", columnList = "pays_id")
        }
)
@NoArgsConstructor
public class Ville{

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pays_id", foreignKey = @ForeignKey(name = "FK_pays_ville"))
    private Pays pays;

    @OneToMany(fetch = LAZY, mappedBy = "villeDepart")
    private Collection<Publication> publicationdep;

    @OneToMany(fetch = LAZY, mappedBy = "villeDestination")
    private Collection<Publication> publicationdes;

    @OneToMany(fetch = LAZY, mappedBy = "villeDepart")
    private Collection<Cible> cibleDepart;

    @OneToMany(fetch = LAZY, mappedBy = "villeDestination")
    private Collection<Cible> cibleDestination;
}
