package com.ankk.tro.controller;

import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.httpbean.*;
import com.ankk.tro.model.*;
import com.ankk.tro.repositories.*;
import com.ankk.tro.services.Firebasemessage;
import com.ankk.tro.services.Messervices;
import com.ankk.tro.testrestemplate.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class ApiController {

    // Attribute :
    private final UtilisateurRepository utilisateurRepository;
    private final PaysRepository paysRepository;
    private final DeviseRepository deviseRepository;
    private final TypePieceRepository typePieceRepository;
    private final VilleRepository villeRepository;
    private final PublicationRepository publicationRepository;
    private final ReservationRepository reservationRepository;
    private final CibleRepository cibleRepository;
    private final ChatRepository chatRepository;
    private final Messervices messervices;
    private final Firebasemessage firebasemessage;

    @Value("${app.firebase-config}")
    private String firebaseConfig;
    FirebaseApp firebaseApp;



    // M E T H O D S :
    @PostConstruct
    private void initialize(){

        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource(firebaseConfig).
                                    getInputStream())).build();
            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            System.out.println("Create FirebaseApp Error : " + e.getMessage());
        }

        //System.out.println(messervices.generatePublicationId("Koffi Konan Aranud", 1));



        //
        /*Utilisateur tp = utilisateurRepository.findAllByOrderByNomAsc().get(0);
        System.out.println("Usr : "+
                trousseOutil.decrypt(tp.getMotdepasse(), javaAesKey, javaAesChain)
        );*/
        /*System.out.println(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).
                format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        System.out.println(ZonedDateTime.now(ZoneId.of( "Africa/Tunis" )).format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME));  */


        /*KeycloakManagerCustom.initialiser("http://localhost:8001/realms/gcatempsreel/protocol/openid-connect/token",
                "backend-adtms", "iKnDPS1anMz3qlDhbRWHB7Wyg8ycsIgO");
        BeanKeycloakAccessToken beanKeycloakAccessToken = KeycloakManagerCustom.getInstance();
        if(beanKeycloakAccessToken != null){
            System.out.println("tokenKeycloak : "+ beanKeycloakAccessToken.getAccessToken());
            System.out.println("Expires IN : "+ beanKeycloakAccessToken.getExpiresIn().toString());
        }

        try{
            GtrApiSendRequest apiRequest = new GtrApiSendRequest();
            apiRequest.setSourceSoftware("TOL");
            apiRequest.setEventType(0);
            apiRequest.setEventDatetime("2024-05-06T10:40:07+01:00");
            apiRequest.setGeneratedDatetime("2024-05-06T10:37:07+01:00");
            apiRequest.setLatitude(46.8115965d);
            apiRequest.setLongitude(4.7763397d);
            apiRequest.setEventCode("GTR_AAR");
            apiRequest.setDescription("The truck has entered geofence area");
            apiRequest.setAlert(false);
            apiRequest.setEnable(false);
            apiRequest.setStatus(1);
            apiRequest.setSeverity(0);
            apiRequest.setOrderNumber("12Az");
            apiRequest.setTmsStopOrder(1);

            JSONObject jObject = new JSONObject();
            jObject.put("sourceSoftware", "TOL");
            jObject.put("eventType", 0);
            jObject.put("eventDatetime", "2024-05-06T10:40:07+01:00");
            jObject.put("generatedDatetime", "2024-05-06T10:37:07+01:00");
            jObject.put("latitude", 46.8115965);
            jObject.put("longitude", 46.8115965);
            jObject.put("eventCode", "GTR_AAR");
            jObject.put("description", "The truck has entered geofence area");
            jObject.put("isAlert", false);
            jObject.put("enable", false);
            jObject.put("status", 1);
            jObject.put("severity", 0);
			jObject.put("orderNumber", "12Az");
			jObject.put("tmsStopOrder", 1);

            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(apiRequest);

            // Create a RestTemplate for making HTTP requests
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new MyRestErrorHandler());
            // Set up the request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(beanKeycloakAccessToken.getAccessToken());
            //headers.set("Authorization", "Bearer " + beanKeycloakAccessToken.getAccessToken());
            //headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);


            System.out.println("Data : "+jObject.toString());
            // Create the HTTP request entity
            HttpEntity<GtrApiSendRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
            // Make the request to Keycloak token endpoint
            // ResponseEntity<EventCreationResponse> response = restTemplate.postForObject(
            EventCreationResponse response = restTemplate.postForObject(
                    "http://localhost:8090/events",
                    requestEntity, EventCreationResponse.class);

            System.out.println("Statut : "+ String.valueOf(response.getCode()));
        }
        catch (Exception ex){
            System.out.println("Exception personalisee : "+ex);
        }*/
    }


    @CrossOrigin("*")
    @PostMapping(path = "/sendmessage")
    public ResponseEntity<?> sendmessage(
            @RequestBody MessageRequest data,
            HttpServletRequest request
    )
    {
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        // Get USER
        Utilisateur utilisateurSender = utilisateurRepository.findById(
                data.getIduser()
                ).orElse(null);
        Utilisateur utilisateurReceiver = utilisateurRepository.findById(
                data.getIdsouscripteur() > 0 ? data.getIdsouscripteur() :
                        publication.getUtilisateur().getId()
        ).orElse(null);
        Chat chat = Chat.builder()
                .message(data.getMessage())
                .identifiant(data.getMessageid())
                .utilisateurSender(utilisateurSender)
                .utilisateurReceiver(utilisateurReceiver)
                .publication(publication)
                .build();
        // Persist :
        chatRepository.save(chat);
        // Notify Publication 'Owner'
        firebasemessage.notifyOwnerAboutNewChat(utilisateurReceiver, utilisateurSender, chat);

        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("id", 0);// Nothing to do with the OBJECT to send back
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/manageuser")
    public ResponseEntity<?> manageuser(
            @RequestBody UserCreationRequest user,
            HttpServletRequest request
    )
    {
        // find user :
        Utilisateur ur = utilisateurRepository.findByEmail(user.getEmail()).orElse(null);
        if(ur == null){
            ur = new Utilisateur();
            ur.setPwd(messervices.generatePwd(
                    (user.getNom().trim() + user.getPrenom().trim())));
        }
        // Feed or Update field :
        ur.setNom(user.getNom().trim());
        ur.setPrenom(user.getPrenom().trim());
        ur.setContact(user.getContact().trim());
        ur.setEmail(user.getEmail());
        ur.setAdresse(user.getAdresse());
        ur.setCodeInvitation("");
        ur.setFcmToken(user.getToken());
        // Process PIECE :
        TypePiece typePiece = typePieceRepository.findByLibelle(user.getTypepieceidentite());
        if(typePiece==null){
            typePiece = new TypePiece();
            typePiece.setLibelle(user.getTypepieceidentite());
            typePieceRepository.save(typePiece);
        }
        ur.setTypePiece(typePiece);
        ur.setNumeroPieceIdentite(user.getNumeropieceidentite());
        // Process on Pays :
        Pays pays = paysRepository.findByAbreviation(user.getAbreviationpays()).orElse(null);
        if(pays == null){
            pays = new Pays();
            pays.setId(user.getIdpays());
            pays.setLibelle(user.getPays());
            pays.setAbreviation(user.getAbreviationpays());
            paysRepository.save(pays);
        }
        ur.setPays(pays);
        utilisateurRepository.save(ur);

        Ville villeResidence = villeRepository.findById(user.getIdville()).orElse(null);
        if(villeResidence == null){
            villeResidence = new Ville();
            villeResidence.setId(user.getIdville());
            villeResidence.setLibelle(user.getVille());
            villeResidence.setPays(pays);
            villeRepository.save(villeResidence);
        }

        // Create DEFAULT 'CIBLE'
        Cible cible = new Cible();
        cible.setUtilisateur(ur);
        cible.setPaysDepart(pays);
        cible.setVilleDepart(villeResidence);
        cible.setPaysDestination(pays);
        cible.setVilleDestination(villeResidence);
        cible.setTopic("");
        cibleRepository.save(cible);

        //
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("userid", ur.getId());
        stringMap.put("typepiece", user.getTypepieceidentite());
        stringMap.put("cibleid", cible.getId());
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(value={"/authenticate"})
    private ResponseEntity<?> authenticate(@RequestBody BeanAuthentification data){
        // Check
        Utilisateur ur = utilisateurRepository.
                findByEmailAndPwd(data.getMail().trim(), data.getPwd().trim()).orElse(null);
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("id", ur != null ? ur.getId() : 0);
        stringMap.put("typepieceidentite", ur != null ? ur.getTypePiece().getLibelle() : "");
        stringMap.put("numeropieceidentite", ur != null ? ur.getNumeroPieceIdentite() : "");
        stringMap.put("nationnalite", ur != null ? ur.getPays().getAbreviation() : "");
        stringMap.put("nom", ur != null ? ur.getNom() : "");
        stringMap.put("prenom", ur != null ? ur.getPrenom() : "");
        stringMap.put("email", ur != null ? ur.getEmail() : "");
        stringMap.put("numero", ur != null ? ur.getContact() : "");
        stringMap.put("adresse", ur != null ? ur.getAdresse() : "");
        stringMap.put("fcmtoken", ur != null ? ur.getFcmToken() : "");
        stringMap.put("pwd", "");
        stringMap.put("codeinvitation", "");
        List<CibleBean> cibleBean = new ArrayList<>();
        List<PublicationBean> publicationBeans = new ArrayList<>();
        List<UserBean> userBeans = new ArrayList<>();
        List<SouscriptionBean> souscriptionsBeans = new ArrayList<>();
        if(ur != null){
            List<Cible> cibles = cibleRepository.findByUtilisateur(ur);
            for(Cible cible : cibles){
                CibleBean cn = new CibleBean();
                cn.setId(cible.getId());
                cn.setPaysdestid(cible.getPaysDestination().getId());
                cn.setVilledestid(cible.getVilleDestination() != null ?
                        cible.getVilleDestination().getId() : 0);
                cn.setPaysdepartid(cible.getPaysDepart().getId());
                cn.setVilledepartid(cible.getVilleDepart() != null ?
                        cible.getVilleDepart().getId() : 0);
                cn.setTopic(cible.getTopic());
                cibleBean.add(cn);
            }
            // Save TOKEN :
            ur.setFcmToken(data.getFcmtoken());
            utilisateurRepository.save(ur);

            // Now look for PUBLICATION attached to CIBLE :
            List<Publication> listePublicationSuscribed =
                publicationRepository.findAllByVilleDepartInAndVilleDestinationIn(
                    cibles.stream().map(Cible::getVilleDepart).toList(),
                    cibles.stream().map(Cible::getVilleDestination).toList()
                );
            for(Publication publication : listePublicationSuscribed){

                Utilisateur publisher = publication.getUtilisateur();
                // Check if this PUBLICATION has been booked by current CUSTOMER
                Reservation reservation = reservationRepository.findByUtilisateur(ur);

                PublicationBean publicationBean = new PublicationBean();
                publicationBean.setId(publication.getId());
                publicationBean.setUserid(publication.getUtilisateur().getId());
                publicationBean.setVilledepart(publication.getVilleDepart().getId());
                publicationBean.setVilledestination(publication.getVilleDestination().getId());
                publicationBean.setDatevoyage(publication.getDateVoyage().
                        truncatedTo(ChronoUnit.SECONDS).
                        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                publicationBean.setDatepublication(publication.getCreationDatetime().
                        truncatedTo(ChronoUnit.SECONDS).
                        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                publicationBean.setReserve(publication.getReserve());
                publicationBean.setActive(reservation != null ?
                        (reservation.getReservationState() == ReservationState.TRAITE ? 2 : 1) : 1);
                publicationBean.setReservereelle(reservation != null ? reservation.getReserve() : 0);
                publicationBean.setSouscripteur(reservation != null ? publisher.getId() : 0);
                publicationBean.setMilliseconds(
                        (int)(publication.getDateVoyage().toEpochSecond() * 1000));
                publicationBean.setIdentifiant(publication.getIdentifiant());
                // New Objects :
                publicationBean.setPrix(publication.getPrix());
                publicationBean.setDevise(publication.getDevise().getId());
                publicationBeans.add(publicationBean);

                // Now get USER who created the PUBLICATION :
                if(reservation != null){
                    UserBean userBean = new UserBean();
                    userBean.setNationalite( publisher.getPays().getAbreviation() );
                    userBean.setNom(publisher.getNom());
                    userBean.setPrenom(publisher.getPrenom());
                    userBean.setAdresse(publisher.getAdresse());
                    userBean.setIduser(publisher.getId());
                    userBeans.add(userBean);
                }
            }

            // Look for Publication created by current USER :
            List<Publication> listePublication = publicationRepository.findAllByUtilisateur(ur);
            for(Publication publication : listePublication) {
                PublicationBean publicationBean = new PublicationBean();
                publicationBean.setId(publication.getId());
                publicationBean.setUserid(publication.getUtilisateur().getId());
                publicationBean.setVilledepart(publication.getVilleDepart().getId());
                publicationBean.setVilledestination(publication.getVilleDestination().getId());
                publicationBean.setDatevoyage(publication.getDateVoyage().
                        truncatedTo(ChronoUnit.SECONDS).
                        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                publicationBean.setDatepublication(publication.getCreationDatetime().
                        truncatedTo(ChronoUnit.SECONDS).
                        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                publicationBean.setReserve(publication.getReserve());
                publicationBean.setActive(1);
                publicationBean.setReservereelle(publication.getReserve());
                publicationBean.setSouscripteur(0);
                publicationBean.setMilliseconds(
                        (int)(publication.getDateVoyage().toEpochSecond() * 1000));
                publicationBean.setIdentifiant(publication.getIdentifiant());
                // New Objects :
                publicationBean.setPrix(publication.getPrix());
                publicationBean.setDevise(publication.getDevise().getId());
                publicationBeans.add(publicationBean);

                // Check if PEOPLE has suscribed to that PUBLICATION :
                List<Reservation> lesReservations = reservationRepository.findAllByPublication(publication);
                for(Reservation reservation : lesReservations){
                    Utilisateur suscriber = reservation.getUtilisateur();
                    UserBean userBean = new UserBean();
                    userBean.setNationalite( suscriber.getPays().getAbreviation() );
                    userBean.setNom(suscriber.getNom());
                    userBean.setPrenom(suscriber.getPrenom());
                    userBean.setAdresse(suscriber.getAdresse());
                    userBean.setIduser(suscriber.getId());
                    userBeans.add(userBean);

                    // Feed
                    SouscriptionBean souscriptionBean = new SouscriptionBean();
                    souscriptionBean.setIdpub(reservation.getPublication().getId());
                    souscriptionBean.setIduser(suscriber.getId());
                    souscriptionBean.setReserve(reservation.getReserve());
                    souscriptionBean.setMillisecondes(
                            reservation.getCreationDatetime().toEpochSecond());
                    souscriptionBean.setStatut(
                        reservation.getReservationState() == ReservationState.TRAITE ? 1 : 0);
                    // Feed :
                    souscriptionsBeans.add(souscriptionBean);
                }
            }
        }
        stringMap.put("cibles", cibleBean);
        stringMap.put("publications", publicationBeans);
        // New Objects
        stringMap.put("publicationowner", userBeans);
        stringMap.put("subscriptions", souscriptionsBeans);
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/managecible")
    public ResponseEntity<?> managecible(
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
        stringMap.put("champ", "");
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/managetravel")
    public ResponseEntity<?> managetravel(
            @RequestBody TravelRequest data,
            HttpServletRequest request
    )
    {
        // Process COUNTRY DEPART:
        Pays paysDepart = paysRepository.findByAbreviation(data.getAbrevpaysdepart()).orElseGet( () -> {
            Pays ps = new Pays();
            ps.setId(data.getIdpaysdepart());
            ps.setLibelle(data.getPaysdepart());
            ps.setAbreviation(data.getAbrevpaysdepart());
            paysRepository.save(ps);
            return ps;
        });
        // Process DEVISE :
        Devise devise = deviseRepository.findById(data.getDeviseid()).orElseGet(
            () -> {
                Devise dev = new Devise();
                dev.setId(data.getDeviseid());
                dev.setLibelle(data.getDeviselib());
                deviseRepository.save(dev);
                return dev;
            }
        );
        // Process TOWN DEPART:
        Ville villeDepart = villeRepository.findByLibelle(data.getVilledepart().trim())
                .orElseGet( () -> {
                    Ville vl = new Ville();
                    vl.setId(data.getIdvilledepart());
                    vl.setLibelle(data.getVilledepart().trim());
                    vl.setPays(paysDepart);
                    villeRepository.save(vl);
                    return vl;
                });
        // Process COUNTRY DESTINATION:
        Pays paysDestination = paysRepository.findByAbreviation(data.getAbrevpaysdestination()).orElseGet( () -> {
            Pays ps = new Pays();
            ps.setId(data.getIdpaysdestination());
            ps.setLibelle(data.getPaysdestination());
            ps.setAbreviation(data.getAbrevpaysdestination());
            paysRepository.save(ps);
            return  ps;
        });
        // Process TOWN DEPART:
        Ville villeDestination = villeRepository.findByLibelle(data.getVilledestination().trim())
                .orElseGet( () -> {
                    Ville vl = new Ville();
                    vl.setId(data.getIdvilledestination());
                    vl.setLibelle(data.getVilledestination().trim());
                    vl.setPays(paysDestination);
                    villeRepository.save(vl);
                    return vl;
                });
        // Work on Travel Request :
        Publication publication = publicationRepository.findById(data.getId()).orElse(null);
        if(publication == null){
            publication = new Publication();
        }
        // Get USER
        Utilisateur utilisateur = utilisateurRepository.findById(data.getUser()).orElse(null);

        // Feed it :
        publication.setVilleDepart(villeDepart);
        publication.setVilleDestination(villeDestination);
        publication.setReserve(data.getReserve());
        publication.setUtilisateur(utilisateur);
        // Convert to OffsetDatetime :
        OffsetDateTime currentDateTime = OffsetDateTime.now(Clock.systemUTC());
        int offsetHour = currentDateTime.getHour();
        int dataHour = Integer.parseInt(data.getHeuregeneration().split(":")[0]);
        int difference = offsetHour - dataHour;
        String offset = "";
        if(difference < 0 && Math.abs(difference) < 10){
            offset = "+0"+ Math.abs(difference) +":00";
        }
        else if(difference < 0 && Math.abs(difference) >= 10){
            offset = "+"+ Math.abs(difference) +":00";
        }
        else if(difference > 0 && Math.abs(difference) < 10){
            offset = "-0"+ difference +":00";
        }
        else if(difference > 0 && Math.abs(difference) >= 10){
            offset = "-"+ difference +":00";
        }
        else if(difference == 0){
            offset = "+00:00";
        }
        //
        publication.setDevise(devise);
        publication.setPrix(data.getPrix());
        publication.setIdentifiant(messervices.generatePublicationId(
                (utilisateur.getNom() + utilisateur.getPrenom()), utilisateur.getId()));
        //publication.setDateVoyage(OffsetDateTime.parse(data.getDate() + "T" + data.getHeure() +"+02:00"));
        publication.setDateVoyage(
                OffsetDateTime.of(
                        LocalDateTime.ofEpochSecond((data.getMilliseconds() / 1000), 0
                                , ZoneOffset.UTC), ZoneOffset.UTC)
        );

        /*OffsetDateTime tamponTime = OffsetDateTime.of(
                LocalDateTime.ofEpochSecond((data.getMilliseconds() / 1000), 0
                        , ZoneOffset.UTC), ZoneOffset.UTC);*/

        //System.out.println("tamponTime : "+ tamponTime.toString());


        /*publication.setDateVoyage(OffsetDateTime.of(
                LocalDateTime.ofEpochSecond(data.getMilliseconds(), 0
                        , ZoneOffset.UTC), ZoneOffset.UTC)
        );*/
        publicationRepository.save(publication);

        // Look for those who are interested :
        List<Cible> cibles = cibleRepository
                .findByVilleDepartAndVilleDestination(villeDepart, villeDestination);
        if(!cibles.isEmpty()){
            // Process :
            String departDestination = villeDepart.getLibelle() + "  ->  " +
                    villeDestination.getLibelle();
            List<String> lesCibles = cibles.stream().map(
                    cible -> cible.getUtilisateur().getFcmToken()).toList();
            if(!lesCibles.isEmpty()){
                firebasemessage.notifySuscriberAboutCible(lesCibles,
                        publication, departDestination);
            }
        }

        //
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("id", publication.getId());
        stringMap.put("date", publication.getCreationDatetime().truncatedTo(ChronoUnit.SECONDS).
                format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        stringMap.put("identifiant", publication.getIdentifiant());
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/managereservation")
    public ResponseEntity<?> managereservation(
            @RequestBody ReservationRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Utilisateur suscriber = utilisateurRepository.findById(data.getIduser()).orElse(null);
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        //
        Reservation reservation = new Reservation();
        reservation.setReserve(data.getReserve());
        reservation.setMontant(data.getMontant());
        reservation.setUtilisateur(suscriber);
        reservation.setPublication(publication);
        reservation.setReservationState(ReservationState.EN_COURS);
        // persist
        reservationRepository.save(reservation);

        // Return response :
        Utilisateur owner = utilisateurRepository.findById(publication.getUtilisateur().getId()).orElse(null);
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("id", owner.getId());
        stringMap.put("nom", owner.getNom());
        stringMap.put("prenom", owner.getPrenom());
        stringMap.put("adresse", owner.getAdresse());
        Pays paysOwner = paysRepository.findById(owner.getPays().getId()).orElse(null);
        stringMap.put("nationnalite", paysOwner.getAbreviation());

        // Notify the publication's OWNER
        Pays paysSuscriber = paysRepository.findById(suscriber.getPays().getId()).orElse(null);
        firebasemessage.notifyOwnerAboutNewReservation(owner,suscriber,publication,paysSuscriber,
                data.getReserve());

        return ResponseEntity.ok(stringMap);
    }

    @CrossOrigin("*")
    @PostMapping(path = "/markdelivery")
    public ResponseEntity<?> markdelivery(
            @RequestBody DeliveryRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Utilisateur suscriber = utilisateurRepository.findById(data.getIduser()).orElse(null);
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        //
        Reservation reservation = reservationRepository.
                findByUtilisateurAndPublication(suscriber, publication);
        // Update :
        reservation.setReservationState(ReservationState.TRAITE);
        reservationRepository.save(reservation);
        // Notify to SUSCRIBER :

        return ResponseEntity.ok(Optional.empty());
    }
}
