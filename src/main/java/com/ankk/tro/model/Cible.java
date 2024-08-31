package com.ankk.tro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(
        indexes = {
                @Index(name = "utilisateur_cible_id_idx", columnList = "utilisateur_id"),
                @Index(name = "pays_cible_dep_id_idx", columnList = "pays_depart_id"),
                @Index(name = "ville_cible_dep_id_idx", columnList = "ville_depart_id"),
                @Index(name = "pays_cible_dest_id_idx", columnList = "pays_dest_id"),
                @Index(name = "ville_cible_dest_id_idx", columnList = "ville_dest_id")
        }
)
@NoArgsConstructor
public class Cible extends AbstractEntity{

    private String topic;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_cible"))
    private Utilisateur utilisateur;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pays_depart_id", foreignKey = @ForeignKey(name = "FK_pays_depart_cible"))
    private Pays paysDepart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ville_depart_id", foreignKey = @ForeignKey(name = "FK_ville_depart_cible"))
    private Ville villeDepart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pays_dest_id", foreignKey = @ForeignKey(name = "FK_pays_dest_cible"))
    private Pays paysDestination;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ville_dest_id", foreignKey = @ForeignKey(name = "FK_ville_dest_cible"))
    private Ville villeDestination;
}
