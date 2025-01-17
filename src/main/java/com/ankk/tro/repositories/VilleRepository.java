package com.ankk.tro.repositories;

import com.ankk.tro.model.Ville;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface VilleRepository extends CrudRepository<Ville, Long> {
    Optional<Ville> findByLibelle(String libelle);
    List<Ville> findAllByOrderByLibelleAsc();
}
