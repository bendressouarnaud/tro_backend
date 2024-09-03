package com.ankk.tro.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsParam extends AbstractEntity{
    private int choix;
    private OffsetDateTime debut;
    private OffsetDateTime fin;
}
