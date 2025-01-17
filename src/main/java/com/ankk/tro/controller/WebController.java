package com.ankk.tro.controller;

import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.enums.SmartphoneType;
import com.ankk.tro.httpbean.*;
import com.ankk.tro.model.*;
import com.ankk.tro.repositories.*;
import com.ankk.tro.services.EmailService;
import com.ankk.tro.services.Firebasemessage;
import com.ankk.tro.services.Messervices;
import io.getstream.chat.java.models.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class WebController {

    // Attributes
    private final BonusRepository bonusRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PaysRepository paysRepository;
    private final DeviseRepository deviseRepository;
    private final TypePieceRepository typePieceRepository;
    private final VilleRepository villeRepository;
    private final PublicationRepository publicationRepository;
    private final ReservationRepository reservationRepository;
    private final ApiRequestRepository apiRequestRepository;
    private final CibleRepository cibleRepository;
    private final CodeFiliationRepository codeFiliationRepository;
    private final ChatRepository chatRepository;
    private final RemboursementRepository remboursementRepository;
    private final NotificationsParamRepository notificationsParamRepository;
    private final LocalParametersRepository localParametersRepository;
    private final Messervices messervices;
    private final Firebasemessage firebasemessage;
    private final EmailService emailService;


    // Methods :
    // Getting TOWNs :
    @CrossOrigin("*")
    @GetMapping(path = "/getcountries")
    public List<DataStringId> getCountries() {
        List<Pays> lesPays = paysRepository.findAllByOrderByLibelleAsc();
        return lesPays.stream()
                .map(this::createLibAndId).toList();
    }

    @CrossOrigin("*")
    @GetMapping(path = "/gettowns")
    public List<TownsByCountry> getTowns() {
        List<Ville> lesVille = villeRepository.findAllByOrderByLibelleAsc();
        return lesVille.stream()
                .map(this::createTownLibAndId).toList();
    }

    private DataStringId createLibAndId(Pays pays) {
        DataStringId dataStringId = new DataStringId();
        dataStringId.setId(pays.getId());
        dataStringId.setLibelle(pays.getLibelle());
        dataStringId.setAbreviation(pays.getAbreviation());
        return dataStringId;
    }

    private TownsByCountry createTownLibAndId(Ville ville) {
        TownsByCountry townsByCountry = new TownsByCountry();
        townsByCountry.setId(ville.getId());
        townsByCountry.setLibelle(ville.getLibelle());
        townsByCountry.setPaysid(ville.getPays().getId());
        return townsByCountry;
    }

    private boolean checkCodeParrainageExistence(String codeparrainage){
        Optional<CodeFiliation> cFiliation = codeFiliationRepository.findByCode(codeparrainage);
        return cFiliation.isPresent();
    }

    @CrossOrigin("*")
    @PostMapping(path = "/managewebuser")
    public ResponseEntity<?> managewebuser(
            @RequestBody UserCreationRequest user,
            HttpServletRequest request
    ) {
        boolean newUser = false;
        // find user :
        Utilisateur ur = utilisateurRepository.findByEmail(user.getEmail()).orElse(null);
        if (ur == null && user.getIduser() == 0) {
            newUser = true;
            ur = new Utilisateur();
            ur.setActive(1);
            ur.setValidateAccount(1);
            ur.setSmartphoneType(user.getSmartphonetype() == 0 ?
                    SmartphoneType.IPHONE : SmartphoneType.ANDROID);
            // Check if exists :
            if (!user.getCodeinvitation().isEmpty()) {
                if (checkCodeParrainageExistence(user.getCodeinvitation())) {
                    ur.setCodeInvitation(user.getCodeinvitation());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
            } else ur.setCodeInvitation("");
            ur.setPwd(messervices.generatePwd(
                    (user.getNom().trim() + user.getPrenom().trim())));
        } else if (ur != null && (user.getIduser() == 0 || ur.getActive() == 0)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // Feed or Update field :
        ur.setNom(user.getNom().trim());
        ur.setPrenom(user.getPrenom().trim());
        ur.setContact(user.getContact().trim());
        ur.setEmail(user.getEmail());
        ur.setAdresse(user.getAdresse());
        ur.setFcmToken(user.getToken());
        // Process PIECE :
        TypePiece typePiece = typePieceRepository.findByLibelle(user.getTypepieceidentite());
        if (typePiece == null) {
            typePiece = new TypePiece();
            typePiece.setLibelle(user.getTypepieceidentite());
            typePieceRepository.save(typePiece);
        }
        ur.setTypePiece(typePiece);
        ur.setNumeroPieceIdentite(user.getNumeropieceidentite());
        // Process on Pays :
        Pays pays = paysRepository.findByAbreviation(user.getAbreviationpays()).orElse(null);
        if (pays == null) {
            pays = new Pays();
            pays.setId(user.getIdpays());
            pays.setLibelle(user.getPays());
            pays.setAbreviation(user.getAbreviationpays());
            paysRepository.save(pays);
        }
        ur.setPays(pays);

        // Process on VILLE :
        Ville villeResidence = villeRepository.findById(user.getIdville()).orElse(null);
        if (villeResidence == null) {
            villeResidence = new Ville();
            villeResidence.setId(user.getIdville());
            villeResidence.setLibelle(user.getVille());
            villeResidence.setPays(pays);
            villeRepository.save(villeResidence);
        }
        ur.setVilleResidence(villeResidence);

        // From there
        if (newUser) {
            NotificationsParam notificationsParam = NotificationsParam.builder()
                    .choix(0)
                    .debut(OffsetDateTime.now(Clock.systemUTC()))
                    .fin(OffsetDateTime.now(Clock.systemUTC()))
                    .build();
            notificationsParamRepository.save(notificationsParam);
            ur.setNotificationsParam(notificationsParam);
        }
        //
        Utilisateur keepUr = utilisateurRepository.save(ur);
        var newToken = "";
        var streamChatId = "";
        if (newUser) {
            // From there, GENERATE his STREAM CHAT 'TOKEN' :
            String iD = messervices.generateCustomUserId(
                    keepUr.getNom(), keepUr.getPrenom(), keepUr.getId());
            streamChatId = iD;
            newToken = User.createToken(iD, null, null);
            keepUr.setStreamChatToken(newToken);
            keepUr.setStreamChatId(streamChatId);
            utilisateurRepository.save(keepUr);
            //System.out.println("STREAM CHAT : "+newToken);

            // Sync :
            emailService.syncUserId(iD, keepUr.getNom());
        }

        // Create DEFAULT 'CIBLE'
        Cible cible = new Cible();
        String codeParrainage = "";
        if (newUser) {
            cible.setUtilisateur(ur);
            cible.setPaysDepart(pays);
            cible.setVilleDepart(villeResidence);
            cible.setPaysDestination(pays);
            cible.setVilleDestination(villeResidence);
            cible.setTopic("");
            cibleRepository.save(cible);

            // Create first CODE :
            CodeFiliation codeFiliation = new CodeFiliation();
            codeParrainage = messervices.generateCodeFiliation(
                    (user.getNom().trim() + " " + user.getPrenom().trim()), ur.getId());
            codeFiliation.setCode(codeParrainage);
            codeFiliation.setUtilisateur(ur);
            codeFiliationRepository.save(codeFiliation);

            // Send MAIL :
            LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
            assert localParameters != null;
            if (localParameters.isEnvoiMail()) {
                emailService.mailCreation("Identifiants de connexion", ur.getEmail(), ur.getPwd());
            }
        }

        //
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("userid", ur.getId());
        stringMap.put("typepiece", user.getTypepieceidentite());
        stringMap.put("cibleid", newUser ? cible.getId() : 0);
        stringMap.put("codeparrainage", codeParrainage);
        stringMap.put("streamchatoken", newToken);
        stringMap.put("streamchatid", streamChatId);
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/authentificationweb")
    public ResponseEntity<?> authentificationweb(
            @RequestBody UserLog userLog,
            HttpServletRequest request
    ) {
        // find user :
        Utilisateur ur = utilisateurRepository.findByEmailAndPwdAndActive(
                userLog.getIdentifiant(), userLog.getMotdepasse(), 1).orElse(null);
        Map<String, Object> stringMap = new HashMap<>();
        if (ur != null) {
            stringMap.put("userid", ur.getId());
            stringMap.put("profil", "commercial");
            stringMap.put("identifiant", ur.getEmail());
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        //
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/getciblesfromuser")
    public List<CibleResponseWeb> getciblesfromuser(
            @RequestBody UserLog userLog,
            HttpServletRequest request
    ) {
        // find user :
        Utilisateur ur = utilisateurRepository.findById(Long.parseLong(userLog.getIdentifiant())).orElse(null);
        List<Cible> lesCibles = cibleRepository.findByUtilisateur(ur);
        List<CibleResponseWeb> reponses = new ArrayList<>();
        for(Cible cible : lesCibles){
            CibleResponseWeb cb = new CibleResponseWeb();
            cb.setIdPaysDepart(cible.getPaysDepart().getId());
            cb.setIdVilleDepart(cible.getVilleDepart().getId());
            cb.setIdPaysDestination(cible.getPaysDestination().getId());
            cb.setIdVilleDestination(cible.getVilleDestination().getId());
            reponses.add(cb);
        }
        return reponses;
    }


    @CrossOrigin("*")
    @PostMapping(path = "/managecibleweb")
    public ResponseEntity<?> managecibleweb(
            @RequestBody CibleRequest data,
            HttpServletRequest request
    )
    {
        Cible cible = cibleRepository.findById(data.getId()).orElse(new Cible());
        Pays paysDepart = paysRepository.findById(data.getIdpaysdep()).orElse(null);
        if(paysDepart == null){
            paysDepart = new Pays();
            paysDepart.setId(data.getIdpaysdep());
            paysDepart.setLibelle(data.getPaysdeplib());
            paysDepart.setAbreviation(data.getPaysdepabrev());
            paysRepository.save(paysDepart);
        }
        Ville villeDepart = villeRepository.findById(data.getIdvilledep()).orElse(null);
        if(villeDepart == null){
            villeDepart = new Ville();
            villeDepart.setId(data.getIdvilledep());
            villeDepart.setLibelle(data.getVilledeplib());
            villeDepart.setPays(paysDepart);
            villeRepository.save(villeDepart);
        }
        // Destination
        Pays paysDestination = paysRepository.findById(data.getIdpaysdest()).orElse(null);
        if(paysDestination == null){
            paysDestination = new Pays();
            paysDestination.setId(data.getIdpaysdest());
            paysDestination.setLibelle(data.getPaysdestlib());
            paysDestination.setAbreviation(data.getPaysdestabrev());
            paysRepository.save(paysDestination);
        }
        Ville villeDestination = villeRepository.findById(data.getIdvilledest()).orElse(null);
        if(villeDestination == null){
            villeDestination = new Ville();
            villeDestination.setId(data.getIdvilledest());
            villeDestination.setLibelle(data.getVilledestlib());
            villeDestination.setPays(paysDestination);
            villeRepository.save(villeDestination);
        }
        Utilisateur ur = utilisateurRepository.
                findById(data.getIduser()).orElse(null);
        // Feed Cible :
        cible.setPaysDepart(paysDepart);
        cible.setVilleDepart(villeDepart);
        cible.setPaysDestination(paysDestination);
        cible.setVilleDestination(villeDestination);
        cible.setTopic(data.getTopic());
        cible.setUtilisateur(ur);
        cibleRepository.save(cible);

        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("idcible", cible.getId());
        return ResponseEntity.ok(stringMap);
    }

    @CrossOrigin("*")
    @PostMapping(path = "/getpublicationattachedtouser")
    public List<PublicationResponse> getpublicationattachedtouser(
            @RequestBody UserLog userLog,
            HttpServletRequest request
    ) {
        // find user :
        Utilisateur ur = utilisateurRepository.findById(Long.parseLong(userLog.getIdentifiant())).orElse(null);
        // PUBLICATION created by OTHERS :
        List<Cible> cibles = cibleRepository.findByUtilisateur(ur);
        List<Publication> listePublication =
                publicationRepository.findAllByDateVoyageGreaterThanEqualAndVilleDepartInAndVilleDestinationIn(
                        OffsetDateTime.now(),
                        cibles.stream().map(Cible::getVilleDepart).toList(),
                        cibles.stream().map(Cible::getVilleDestination).toList()
                );
        // PUBLICATION created by CURRENT USER :
        listePublication.addAll(publicationRepository.findAllByDateVoyageGreaterThanEqualAndUtilisateur(
                OffsetDateTime.now(), ur));
        List<PublicationResponse> retour = new ArrayList<>();
        for(Publication publication : listePublication){
            PublicationResponse pe = new PublicationResponse();
            pe.setIdvilledep(publication.getVilleDepart().getId());
            pe.setIdvilledest(publication.getVilleDestination().getId());
            pe.setId(pe.getId());
            assert ur != null;
            pe.setProvider(Objects.equals(publication.getUtilisateur().getId(), ur.getId()) ? 1 : 0 ); // Created by OTHERS
            pe.setDate(publication.getDateVoyage().toLocalDate().toString());
            pe.setHeure(publication.getDateVoyage().toLocalTime().toString());
            //
            pe.setPrix(publication.getPrix());
            retour.add(pe);
        }
        return retour;
    }
}
