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
                @Index(name = "utilisateur_bonus_id_idx", columnList = "utilisateur_id")
        }
)
@NoArgsConstructor
public class Bonus extends AbstractEntity{

    private Double montant;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_bonus"))
    private Utilisateur utilisateur;

}