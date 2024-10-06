package com.ankk.tro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Collection;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(
        indexes = {
                @Index(name = "utilisateur_publication_id_idx", columnList = "utilisateur_id"),
                @Index(name = "ville_depart_publication_id_idx", columnList = "ville_depart_id"),
                @Index(name = "ville_dest_publication_id_idx", columnList = "ville_dest_id"),
                @Index(name = "identifiant_annonce_publication_idx", columnList = "identifiant"),
                @Index(name = "devise_publication_idx", columnList = "devise_id")
        }
)
@NoArgsConstructor
public class Publication extends AbstractEntity{
    private OffsetDateTime dateVoyage;
    private Integer reserve;
    private Integer prix;
    private String identifiant;
    private boolean active;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ville_depart_id", foreignKey = @ForeignKey(name = "FK_ville_depart_publication"))
    private Ville villeDepart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ville_dest_id", foreignKey = @ForeignKey(name = "FK_ville_dest_publication"))
    private Ville villeDestination;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_publication"))
    private Utilisateur utilisateur;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "devise_id", foreignKey = @ForeignKey(name = "FK_devise_publication"))
    private Devise devise;

    @OneToMany(fetch = LAZY, mappedBy = "publication")
    private Collection<Reservation> reservations;

}
