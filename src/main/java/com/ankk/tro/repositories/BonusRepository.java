package com.ankk.tro.repositories;

import com.ankk.tro.model.Bonus;
import com.ankk.tro.model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BonusRepository extends CrudRepository<Bonus, Long> {
    List<Bonus> findByUtilisateur(Utilisateur utilisateur);
}
