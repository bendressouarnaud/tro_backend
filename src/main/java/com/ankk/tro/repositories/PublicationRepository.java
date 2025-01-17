package com.ankk.tro.repositories;

import com.ankk.tro.model.Publication;
import com.ankk.tro.model.Utilisateur;
import com.ankk.tro.model.Ville;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PublicationRepository extends CrudRepository<Publication, Long> {

    List<Publication> findAllByVilleDepartInAndVilleDestinationIn(
            List<Ville> dep, List<Ville> dest);
    List<Publication> findAllByDateVoyageLessThanEqualAndVilleDepartInAndVilleDestinationIn(
            OffsetDateTime datevoyage,
            List<Ville> dep, List<Ville> dest);

    List<Publication> findAllByDateVoyageGreaterThanEqualAndVilleDepartInAndVilleDestinationIn(
            OffsetDateTime datevoyage,
            List<Ville> dep, List<Ville> dest);
    List<Publication> findAllByUtilisateur(Utilisateur utilisateur);
    List<Publication> findAllByDateVoyageGreaterThanEqualAndUtilisateur(OffsetDateTime datevoyage,
        Utilisateur utilisateur);
    List<Publication> findAllByOrderByIdAsc();
}
