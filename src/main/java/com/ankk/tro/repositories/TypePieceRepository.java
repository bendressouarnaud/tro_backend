package com.ankk.tro.repositories;

import com.ankk.tro.model.TypePiece;
import org.springframework.data.repository.CrudRepository;

public interface TypePieceRepository extends CrudRepository<TypePiece, Long> {
    TypePiece findByLibelle(String libelle);
}
