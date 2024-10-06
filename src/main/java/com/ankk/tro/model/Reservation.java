package com.ankk.tro.model;

import com.ankk.tro.enums.ReservationState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(
        indexes = {
                @Index(name = "utilisateur_publication_reservation_id_idx", columnList = "utilisateur_id"),
                @Index(name = "publication_publication_reservation_id_idx", columnList = "publication_id")
        }
)
@NoArgsConstructor
public class Reservation extends AbstractEntity{

    private int montant;
    private int reserve;

    @Enumerated(EnumType.ORDINAL)
    private ReservationState reservationState;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_id", foreignKey = @ForeignKey(name = "FK_utilisateur_reservation"))
    private Utilisateur utilisateur;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "publication_id", foreignKey = @ForeignKey(name = "FK_publication_reservation"))
    private Publication publication;

    @OneToMany(fetch = LAZY, mappedBy = "reservation")
    private Collection<ApiRequest> apiRequests;

    @OneToMany(fetch = LAZY, mappedBy = "reservation")
    private Collection<Remboursement> remboursements;
}
