package com.ankk.tro.repositories;

import com.ankk.tro.model.Pays;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PaysRepository extends CrudRepository<Pays, Long> {
    Optional<Pays> findByAbreviation(String abreviation);
    List<Pays> findAllByOrderByLibelleAsc();
}
