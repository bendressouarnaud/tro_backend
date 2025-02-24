package com.ankk.tro.repositories;
import com.ankk.tro.model.Utilisateur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByEmailAndPwd(String email, String pwd);
    Optional<Utilisateur> findByIdAndPwdAndValidateAccount(Long id, String pwd, Integer validateAccount);
    Optional<Utilisateur> findByIdAndEmail(Long id, String email);
    Optional<Utilisateur> findByEmailAndPwdAndActive(String email, String pwd, int active);
    List<Utilisateur> findAllByOrderByNomAsc();

    // Get History of TODAY :
    /*@Query(value = "SELECT * FROM Utilisateur j WHERE DATALENGTH(j.fcmtoken) > 0 and j.iduser <> ?1",
            nativeQuery = true)
    List<Utilisateur> findAllUsersWithNoFcmtoken(int iduser);*/
}
