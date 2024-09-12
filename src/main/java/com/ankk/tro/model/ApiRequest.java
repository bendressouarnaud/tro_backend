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
                @Index(name = "reservation_apirequest_id_idx", columnList = "reservation_id")
        }
)
@NoArgsConstructor
public class ApiRequest extends AbstractEntity{

    private String apiId;
    private String launchUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reservation_id", foreignKey = @ForeignKey(name = "FK_reservation_apirequest"))
    private Reservation reservation;

}
