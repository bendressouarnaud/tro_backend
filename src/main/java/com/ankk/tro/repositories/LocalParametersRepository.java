package com.ankk.tro.repositories;

import com.ankk.tro.model.LocalParameters;
import com.ankk.tro.model.Utilisateur;
import com.ankk.tro.model.Ville;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocalParametersRepository extends CrudRepository<LocalParameters, Long> {
}