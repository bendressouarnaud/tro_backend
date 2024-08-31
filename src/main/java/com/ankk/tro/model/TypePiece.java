package com.ankk.tro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
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
@NoArgsConstructor
public class TypePiece extends AbstractEntity{

    private String libelle;

    @OneToMany(fetch = LAZY, mappedBy = "typePiece")
    private Collection<Utilisateur> utilisateurs;
}
