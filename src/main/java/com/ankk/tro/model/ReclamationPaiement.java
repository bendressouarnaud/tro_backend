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
                @Index(name = "reclamation_paiement_id_idx", columnList = "utilisateur_id")
        }
)
@NoArgsConstructor
public class ReclamationPaiement extends AbstractEntity{

    private long montant;
    private boolean montantRegle;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_reclamation_paiement"))
    private Utilisateur utilisateur;

}
