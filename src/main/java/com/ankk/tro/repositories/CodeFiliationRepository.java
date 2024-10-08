package com.ankk.tro.repositories;

import com.ankk.tro.model.CodeFiliation;
import com.ankk.tro.model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CodeFiliationRepository extends CrudRepository<CodeFiliation, Long> {

    List<CodeFiliation> findByUtilisateur(Utilisateur utilisateur);
    List<CodeFiliation> findByUtilisateurAndActive(Utilisateur utilisateur, boolean active);
    Optional<CodeFiliation> findByCode(String code);

}
