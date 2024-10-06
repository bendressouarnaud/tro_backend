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
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "reservation_remboursement_id_idx", columnList = "reservation_id")
        }
)
public class Remboursement extends AbstractEntity{

    private Integer montant;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reservation_id", foreignKey = @ForeignKey(name = "FK_reservation_remboursement"))
    private Reservation reservation;
}
