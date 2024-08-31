package com.ankk.tro.repositories;

import com.ankk.tro.model.Cible;
import com.ankk.tro.model.Utilisateur;
import com.ankk.tro.model.Ville;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CibleRepository extends CrudRepository<Cible, Long> {
    List<Cible> findByUtilisateur(Utilisateur utilisateur);
    List<Cible> findByVilleDepartAndVilleDestination(Ville depart, Ville destination);
}
