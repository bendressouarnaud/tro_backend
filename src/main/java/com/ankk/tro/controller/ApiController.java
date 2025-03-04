package com.ankk.tro.controller;

import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.enums.SmartphoneType;
import com.ankk.tro.httpbean.*;
import com.ankk.tro.model.*;
import com.ankk.tro.repositories.*;
import com.ankk.tro.services.EmailService;
import com.ankk.tro.services.Firebasemessage;
import com.ankk.tro.services.Messervices;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.getstream.chat.java.models.ChannelType;
import io.getstream.chat.java.models.User;
import io.getstream.chat.java.services.framework.DefaultClient;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class ApiController {

    // Attribute :
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
    private final ReclamationPaiementRepository reclamationPaiementRepository;
    private final Messervices messervices;
    private final Firebasemessage firebasemessage;
    private final EmailService emailService;

    @Value("${app.firebase-config}")
    private String firebaseConfig;
    FirebaseApp firebaseApp;

    @Value("${sfp.wave.token}")
    private String waveToken;
    @Value("${sfp.wave.apiurl}")
    private String waveUrl;
    @Value("${backend.web.url}")
    private String backendWebUrl;

    @Value("${owner.payment.percentagewithoutgodfather}")
    private double ownerPaymentWithoutGodfather;
    @Value("${owner.payment.percentagewithgodfather}")
    private double ownerPaymentWithGodfather;
    @Value("${godfather.payment.percentage}")
    private double godfatherPayment;

    @Value("${io.getstream.chat.secretKey}")
    private String STREAM_SECRET;
    @Value("${io.getstream.chat.apiKey}")
    private String STREAM_KEY;




    // M E T H O D S :
    @PostConstruct
    private void initialize(){

        /*String getHourOffset = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).
                format(DateTimeFormatter.ISO_OFFSET_DATE_TIME).split("T")[1];
        String offSet = getHourOffset.substring(8);
        System.out.println("offSet : "+ offSet);*/

        /*String getHourOffset = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).
                format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println("offSet : "+ getHourOffset);*/

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

        try{
            // Set STREAM CONFIGURATION
            var properties = new Properties();
            properties.put(DefaultClient.API_KEY_PROP_NAME, "tbyj8qz6ucx7");
            properties.put(DefaultClient.API_SECRET_PROP_NAME, "c66awuctzf4nfpv7a4bahuee4mrw2v2js9tx9u2w7t4zrbyp53jfq7uj7gtnsxrq");
            var client = new DefaultClient(properties);
            DefaultClient.setInstance(client);

            /*Utilisateur dakaud = utilisateurRepository.findById(2l).orElse(null);
            dakaud.setSmartphoneType(SmartphoneType.IPHONE);
            utilisateurRepository.save(dakaud);

            Utilisateur yakasse = utilisateurRepository.findById(3l).orElse(null);
            yakasse.setSmartphoneType(SmartphoneType.ANDROID);
            utilisateurRepository.save(yakasse);
             */


            /*Iterator<Utilisateur> users = utilisateurRepository.findAll().iterator();
            while(users.hasNext()) {
                Utilisateur element = users.next();
                if(element.getPrenom().contains("ric")){
                    System.out.println( element.getId() +"  ---   Nom : " +element.getNom() +
                            "  ---   Pwd : " +element.getPwd() + "  ---   Smartphone : " +element.getSmartphoneType()
                            + "  ---   mail : " +element.getEmail()
                            + "  ---   active : " +element.getActive());
                }
            }*/

            // Init 'LocalParameters' table
            LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
            if(localParameters == null){
                localParameters = new LocalParameters();
                localParameters.setId(1L);
                localParameters.setEnvoiMail(true);
                localParametersRepository.save(localParameters);

                boolean newUser = false;
                // find user :
                Utilisateur ur = new Utilisateur();
                ur.setActive(1);
                ur.setValidateAccount(0);
                ur.setSmartphoneType(SmartphoneType.ANDROID);
                ur.setCodeInvitation("");
                ur.setPwd(messervices.generatePwd("KONIN PONAN ARISTIDE"));
                // Feed or Update field :
                ur.setNom("KONIN");
                ur.setPrenom("PONAN ARISTIDE");
                ur.setContact("0707640051");
                ur.setEmail("bendressoukonan@gmail.com");
                ur.setAdresse("4 Rue de Charente");
                ur.setFcmToken("c7WulpQSCUA_tleR7Qan2b:APA91bHromUaqpcMxO10NcTrqqZG3UKAFUKgUWCB75pS4PNBIJqYS7oLUXLsIpti7tbu2BpH-LmsocDbskdhpio-co1vQWpz_q_t2yNGSio6WxIasRBDun4");
                // Process PIECE :
                TypePiece typePiece = typePiece = new TypePiece();
                typePiece.setLibelle("CNI");
                typePieceRepository.save(typePiece);

                ur.setTypePiece(typePiece);
                ur.setNumeroPieceIdentite("1234XXX");
                // Process on Pays :
                Pays pays = new Pays();
                pays.setId(2L);
                pays.setLibelle("Côte d'ivoire");
                pays.setAbreviation("CV");
                paysRepository.save(pays);
                ur.setPays(pays);

                // Process on VILLE :
                Ville villeResidence = new Ville();
                villeResidence.setId(55L);
                villeResidence.setLibelle("Bouaké");
                villeResidence.setPays(pays);
                villeRepository.save(villeResidence);
                ur.setVilleResidence(villeResidence);

                // From there
                NotificationsParam notificationsParam = NotificationsParam.builder()
                        .choix(0)
                        .debut(OffsetDateTime.now(Clock.systemUTC()))
                        .fin(OffsetDateTime.now(Clock.systemUTC()))
                        .build();
                notificationsParamRepository.save(notificationsParam);
                ur.setNotificationsParam(notificationsParam);
                //
                Utilisateur keepUr = utilisateurRepository.save(ur);
                var newToken = "";
                var streamChatId = "";
                // From there, GENERATE his STREAM CHAT 'TOKEN' :
                String iD = messervices.generateCustomUserId(
                        keepUr.getNom(), keepUr.getPrenom(), keepUr.getId());
                streamChatId = iD;
                newToken = User.createToken(iD, null, null);
                keepUr.setStreamChatToken(newToken);
                keepUr.setStreamChatId(streamChatId);
                utilisateurRepository.save(keepUr);

                // Sync :
                emailService.syncUserId(iD, keepUr.getNom());

                // Create DEFAULT 'CIBLE'
                Cible cible = new Cible();
                String codeParrainage = "";
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
                        (ur.getNom().trim() + " " + ur.getPrenom().trim()), ur.getId());
                codeFiliation.setCode(codeParrainage);
                codeFiliation.setUtilisateur(ur);
                codeFiliationRepository.save(codeFiliation);
            }

            /*var token = User.createToken("john", null, null);
            System.out.println("John : " + token);

            var usersUpsertRequest = User.upsert();
            usersUpsertRequest.user(User.UserRequestObject.builder().id("john").name("John").build());
            var response = usersUpsertRequest.request();

            System.out.println("response : " + response.toString());
            */

            // observe current grants of the channel type
            /*var response = ChannelType.get("messaging").request();
            System.out.println("Grants : "+response.getGrants());
            // update "channel_member" role grants in "messaging" scope
            var grants = new HashMap<String, List<String>>();
            grants.put("channel_member", List.of(
                    "read-channel",     // allow access to the channel
                    "create-message",    // create messages in the channel
                    "update-message-owner", // update own user messages
                    "delete-message-owner" // delete own user messages
            ));
            ChannelType.update("messaging").grants(grants).request();*/
        }
        catch (Exception e) {
            System.out.println("STREAM CHAT Error : " + e.getMessage());
        }
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
    @PostMapping(path = "/refreshfiliation")
    public ResponseEntity<?> refreshfiliation(
            @RequestBody RefreshRequest data,
            HttpServletRequest request
    )
    {
        Utilisateur user = utilisateurRepository.findById(data.getIduser()).orElse(null);
        String codeParrainage= "";
        if(user != null){
            // Create new CODE :
            CodeFiliation codeFiliation = new CodeFiliation();
            codeParrainage = messervices.generatePublicationId(
                    (user.getNom().trim() + " " + user.getPrenom().trim()), user.getId());
            codeFiliation.setCode(codeParrainage);
            codeFiliation.setUtilisateur(user);
            codeFiliationRepository.save(codeFiliation);

            // Get BONUS :
            List<Bonus> lesBonus = bonusRepository.findByUtilisateur(user);
            double bonus = lesBonus.isEmpty() ? 0 :
                    lesBonus.stream().max(Comparator.comparing(Bonus::getId)).get().getMontant();
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("parrainage", codeParrainage);
            stringMap.put("bonus", bonus);
            return ResponseEntity.ok(stringMap);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @CrossOrigin("*")
    @PostMapping(path = "/requestpayment")
    public ResponseEntity<?> requestpayment(
            @RequestBody RequestPaymentBean data,
            HttpServletRequest request
    )
    {
        Utilisateur user = utilisateurRepository.findById(data.getIduser()).orElse(null);
        if(user != null){
            // Add a new ROW :
            ReclamationPaiement reclamationPaiement = new ReclamationPaiement();
            reclamationPaiement.setMontant(data.getAmount());
            reclamationPaiement.setUtilisateur(user);
            reclamationPaiement.setMontantRegle(false);
            // Persist :
            reclamationPaiementRepository.save(reclamationPaiement);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @CrossOrigin("*")
    @PostMapping(path = "/validatemailaccount")
    public ResponseEntity<?> validatemailaccount(
            @RequestBody ValidationAccountRequest data,
            HttpServletRequest request
    )
    {
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("detail", "");
        Utilisateur utilisateur = utilisateurRepository.findByIdAndPwdAndValidateAccount(
                data.getIduser(), data.getCode(), 0
        ).orElse(null);
        if(utilisateur != null){
            // Update :
            utilisateur.setValidateAccount(1);
            utilisateurRepository.save(utilisateur);
            stringMap.put("message", "ok");
        }
        else{
            stringMap.put("message", "error");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/resendpassword")
    public ResponseEntity<?> resendpassword(
            @RequestBody ValidationAccountRequest data,
            HttpServletRequest request
    )
    {
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("detail", "");
        Utilisateur utilisateur = utilisateurRepository.findByIdAndEmail(
                data.getIduser(), data.getCode()
        ).orElse(null);
        if(utilisateur != null){
            // Send the mail :
            emailService.mailCreation("Rappel mot de passe", "***", utilisateur.getPwd());
            stringMap.put("message", "ok");
        }
        else{
            stringMap.put("message", "error");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/manageuser")
    public ResponseEntity<?> manageuser(
            @RequestBody UserCreationRequest user,
            HttpServletRequest request
    )
    {
        boolean newUser = false;
        // find user :
        Utilisateur ur = utilisateurRepository.findByEmail(user.getEmail().trim()).orElse(null);
        if(ur == null && user.getIduser() == 0){
            newUser = true;
            ur = new Utilisateur();
            ur.setActive(1);
            ur.setValidateAccount(0);
            ur.setSmartphoneType(user.getSmartphonetype() == 0 ?
                    SmartphoneType.IPHONE : SmartphoneType.ANDROID);
            // Check if exists :
            if(!user.getCodeinvitation().isEmpty()){
                if(checkCodeParrainageExistence(user.getCodeinvitation())){
                    ur.setCodeInvitation(user.getCodeinvitation());
                }
                else{
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
            }
            else ur.setCodeInvitation("");
            ur.setPwd(messervices.generatePwd(
                    (user.getNom().trim() + " " + user.getPrenom().trim())));
        }
        else if(ur != null && (user.getIduser() == 0 || ur.getActive() == 0)){
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

        // Process on VILLE :
        Ville villeResidence = villeRepository.findById(user.getIdville()).orElse(null);
        if(villeResidence == null){
            villeResidence = new Ville();
            villeResidence.setId(user.getIdville());
            villeResidence.setLibelle(user.getVille());
            villeResidence.setPays(pays);
            villeRepository.save(villeResidence);
        }
        ur.setVilleResidence(villeResidence);

        // From there
        if(newUser){
            NotificationsParam notificationsParam = NotificationsParam.builder()
                    .choix(0)
                    .debut(OffsetDateTime.now(Clock.systemUTC()))
                    .fin(OffsetDateTime.now(Clock.systemUTC()))
                    .build();
            notificationsParamRepository.save(notificationsParam);
            ur.setNotificationsParam(notificationsParam);
        }
        //
        ur.setStreamChatToken(""); // DEFAULT VALUES
        ur.setStreamChatId("");    // DEFAULT VALUES
        Utilisateur keepUr = utilisateurRepository.save(ur);
        /*var newToken = "";
        var streamChatId = "";
        if(newUser){
            // From there, GENERATE his STREAM CHAT 'TOKEN' :
            String iD = messervices.generateCustomUserId(
                    keepUr.getNom(), keepUr.getPrenom(), keepUr.getId());
            streamChatId = iD;
            newToken = User.createToken(iD, null, null);
            keepUr.setStreamChatToken(newToken);
            keepUr.setStreamChatId(streamChatId);
            utilisateurRepository.save(keepUr);

            // Sync :
            emailService.syncUserId(iD, keepUr.getNom());
        }*/

        // Create DEFAULT 'CIBLE'
        Cible cible = new Cible();
        String codeParrainage = "";
        if(newUser) {
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
            if(localParameters.isEnvoiMail()){
                emailService.mailCreation("Identifiants de connexion", ur.getEmail(), ur.getPwd());
            }
        }

        //
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("userid", ur.getId());
        stringMap.put("typepiece", user.getTypepieceidentite());
        stringMap.put("cibleid", newUser ? cible.getId() : 0);
        stringMap.put("codeparrainage", codeParrainage);
        stringMap.put("streamchatoken", ""); //newToken
        stringMap.put("streamchatid", ""); // streamChatId
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(value={"/authenticate"})
    private ResponseEntity<?> authenticate(@RequestBody BeanAuthentification data){
        // Check
        Utilisateur ur = utilisateurRepository.
                findByEmailAndPwdAndActive(data.getMail().trim(),
                        data.getPwd().trim(), 1).orElse(null);
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
        stringMap.put("streamtoken", ur != null ? ur.getStreamChatToken() : "");
        stringMap.put("streamchatid", ur != null ? ur.getStreamChatId() : "");
        stringMap.put("pwd", "");
        stringMap.put("codeinvitation", ur != null ? ur.getCodeInvitation() : "");
        stringMap.put("villeresidence", ur != null ? ur.getVilleResidence().getId() : 0);
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
            // Update
            ur.setSmartphoneType(data.getSmartphonetype() == 0 ?
                    SmartphoneType.IPHONE : SmartphoneType.ANDROID);
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
                Reservation reservation = reservationRepository.
                        findByUtilisateurAndPublicationAndReservationState(ur, publication, ReservationState.EFFECTUE);

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
                        (int)(publication.getDateVoyage().toInstant().toEpochMilli()));
                publicationBean.setIdentifiant(publication.getIdentifiant());
                // New Objects :
                publicationBean.setPrix(publication.getPrix());
                publicationBean.setDevise(publication.getDevise().getId());
                publicationBean.setRead(1);
                publicationBean.setStreamchannelid(reservation != null ? (
                        reservation.getStreamChatId() != null ?
                                reservation.getStreamChatId() : "") : "");
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
                        (int)(publication.getDateVoyage().toInstant().toEpochMilli()));
                publicationBean.setIdentifiant(publication.getIdentifiant());
                // New Objects :
                publicationBean.setPrix(publication.getPrix());
                publicationBean.setDevise(publication.getDevise().getId());
                publicationBean.setRead(1);
                publicationBean.setStreamchannelid("");
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
                            reservation.getCreationDatetime().toInstant().toEpochMilli());
                    souscriptionBean.setStatut(
                        reservation.getReservationState() == ReservationState.TRAITE ? 1 : 0);
                    souscriptionBean.setChannelid(reservation.getStreamChatId() != null ?
                            reservation.getStreamChatId() : "");
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

        // Look for his PARRAINAGE CODE :
        String codeParrainage = "";
        double leBonus = 0;
        if(ur != null) {
            CodeFiliation codeFiliation = ur.getCodeFiliations().stream().max(
                    Comparator.comparing(CodeFiliation::getId)).get();
            codeParrainage = codeFiliation.getCode();

            Optional<Bonus> bonus = ur.getBonuses().stream().max(
                    Comparator.comparing(Bonus::getId));
            if(bonus.isPresent()){
                leBonus = bonus.get().getMontant();
            }
        }
        stringMap.put("codeparrainage", codeParrainage);
        stringMap.put("bonus", leBonus);

        return ur != null ?
            ResponseEntity.ok(stringMap) :
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @CrossOrigin("*")
    @PostMapping(path = "/managecible")
    public ResponseEntity<?> managecible(
            @RequestBody CibleRequest data,
            HttpServletRequest request
    )
    {
        Cible cible = cibleRepository.findById(data.getId()).orElse(new Cible());
        Pays paysDepart = paysRepository.findByAbreviation(data.getPaysdepabrev()).orElseGet( () -> {
            Pays ps = new Pays();
            ps.setId(data.getIdpaysdep());
            ps.setLibelle(data.getPaysdeplib());
            ps.setAbreviation(data.getPaysdepabrev());
            paysRepository.save(ps);
            return ps;
        });
        Ville villeDepart = villeRepository.findById(data.getIdvilledep()).orElse(null);
        if(villeDepart == null){
            villeDepart = new Ville();
            villeDepart.setId(data.getIdvilledep());
            villeDepart.setLibelle(data.getVilledeplib());
            villeDepart.setPays(paysDepart);
            villeRepository.save(villeDepart);
        }
        // Destination
        Pays paysDestination = paysRepository.findByAbreviation(data.getPaysdestabrev()).orElseGet( () -> {
            Pays ps = new Pays();
            ps.setId(data.getIdpaysdest());
            ps.setLibelle(data.getPaysdestlib());
            ps.setAbreviation(data.getPaysdestabrev());
            paysRepository.save(ps);
            return ps;
        });
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

        List<PublicationBean> publicationBeans = new ArrayList<>();
        // Find PUBLICATION that matches this CIBLE
        List<Publication> listePublicationAvailables =
                publicationRepository.findAllByDateVoyageGreaterThanEqualAndVilleDepartInAndVilleDestinationIn(
                        OffsetDateTime.now(Clock.systemUTC()),
                        Stream.of(cible.getVilleDepart()).toList(),
                        Stream.of(cible.getVilleDestination()).toList()
                );

        for(Publication publication : listePublicationAvailables){
            Utilisateur publisher = publication.getUtilisateur();
            // Check if this PUBLICATION has been booked by current CUSTOMER
            Reservation reservation = reservationRepository.
                    findByUtilisateurAndPublication(ur, publication);

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
            publicationBean.setReservereelle(0);
            publicationBean.setSouscripteur(reservation != null ? publisher.getId() : 0);
            publicationBean.setMilliseconds(
                    (int)(publication.getDateVoyage().toInstant().toEpochMilli()));
            publicationBean.setIdentifiant(publication.getIdentifiant());
            // New Objects :
            publicationBean.setPrix(publication.getPrix());
            publicationBean.setDevise(publication.getDevise().getId());
            publicationBean.setRead(1);
            publicationBean.setStreamchannelid(reservation != null ? (
                    reservation.getStreamChatId() != null ?
                            reservation.getStreamChatId() : "") : "");
            publicationBeans.add(publicationBean);


        }
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("idcible", cible.getId());
        stringMap.put("champ", "");
        stringMap.put("publications", publicationBeans);
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/cancelsuscription")
    public ResponseEntity<?> cancelsuscription(
            @RequestBody DeliveryRequest data,
            HttpServletRequest request
    )
    {
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        Utilisateur utilisateur = utilisateurRepository.findById(data.getIduser()).orElse(null);
        Reservation reservation = reservationRepository.
                findByUtilisateurAndPublicationAndReservationState(
                        utilisateur, publication, ReservationState.EFFECTUE
                );
        if(reservation != null){
            // Cancel it :
            reservation.setReservationState(ReservationState.RESILIE);
            reservationRepository.save(reservation);
            // Notify OWNER :
            firebasemessage.notifyOwnerAboutSubscriptionCancellation(
                    publication.getUtilisateur(), publication, utilisateur.getId());
            return ResponseEntity.ok(Optional.empty());
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
    }


    @CrossOrigin("*")
    @PostMapping(path = "/canceltravel")
    public ResponseEntity<?> canceltravel(
            @RequestBody DeliveryRequest data,
            HttpServletRequest request
    )
    {
        //
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        if(publication != null){
            publication.setActive(false);
            publicationRepository.save(publication);
            // Pick all SOUSCRIPTION :
            List<Reservation> listeReservation = reservationRepository.
                    findAllByPublicationAndReservationState(publication, ReservationState.EFFECTUE);
            for(Reservation reservation : listeReservation){
                reservation.setReservationState(ReservationState.ANNULE);
                reservationRepository.save(reservation);
                Utilisateur suscriber = reservation.getUtilisateur();
                if(reservation.getMontant() > 0) {
                    // New LINE for REIMBURSEMENT
                    Remboursement remboursement = new Remboursement();
                    remboursement.setMontant(reservation.getMontant());
                    remboursement.setReservation(reservation);
                    remboursementRepository.save(remboursement);
                    // Notify BY MAIL :
                    String amountToRepay = NumberFormat.getInstance(Locale.FRENCH).format(
                            reservation.getMontant()
                    );
                    emailService.notificationRemboursement("Remboursement paiement", suscriber,
                            amountToRepay, publication.getIdentifiant());
                }
                // Notify SUSCRIBER :
                firebasemessage.notifySuscriberAboutPublicationCancellation(suscriber,
                        publication);
            }
            return ResponseEntity.ok(Optional.empty());
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
    }


    @CrossOrigin("*")
    @PostMapping(path = "/managetravel")
    public ResponseEntity<?> managetravel(
            @RequestBody TravelRequest data,
            HttpServletRequest request
    )
    {
        boolean updatedPublication = false;
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
        List<RefreshReservationBean> listeRefresh = new ArrayList<>();
        Publication publication = publicationRepository.findById(data.getId()).orElse(null);
        if(publication == null){
            publication = new Publication();
            publication.setActive(true);
        }
        else{
            updatedPublication = true;
            // Not NULL, check USERS who already suscribed and adjust their booking :
            if(data.getReserve() != publication.getReserve()) {
                List<Reservation> listeReservation = reservationRepository.
                        findAllByPublicationAndReservationState(publication, ReservationState.EFFECTUE);
                for (Reservation reservation : listeReservation) {
                    int newReservation = ( data.getReserve() * reservation.getReserve() ) / publication.getReserve();
                    // Adjust Price :
                    int newPriceToPay = newReservation * publication.getPrix();
                    if(newPriceToPay < reservation.getMontant()){
                        // Track that :
                        Remboursement remboursement = new Remboursement();
                        int montantRembourse = reservation.getMontant() - newPriceToPay;
                        remboursement.setMontant(montantRembourse);
                        remboursement.setReservation(reservation);
                        remboursementRepository.save(remboursement);
                        // Pay the difference BACK to SUSCRIBERS
                        reservation.setMontant(newPriceToPay);
                        // Send MAIL :
                        String amountToRepay = NumberFormat.getInstance(Locale.FRENCH).format(montantRembourse);
                        Utilisateur cUr = reservation.getUtilisateur();
                        emailService.notificationRemboursement("Remboursement paiement", cUr, amountToRepay,
                                publication.getIdentifiant());
                    }
                    // Feed :
                    RefreshReservationBean refreshReservationBean = new RefreshReservationBean();
                    refreshReservationBean.setIdpub(reservation.getPublication().getId());
                    refreshReservationBean.setIduser(reservation.getUtilisateur().getId());
                    refreshReservationBean.setReserve(newReservation);
                    listeRefresh.add(refreshReservationBean);
                    reservation.setReserve(newReservation);
                    // Persist :
                    reservationRepository.save(reservation);

                    // Notify USER who suscribed in order his QUANTITY be updated automatically :
                    Utilisateur suscriber = reservation.getUtilisateur();
                    firebasemessage.notifySuscriberAboutPublicationUpdate(suscriber, publication, newReservation);
                }
            }
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
        // Pick the TOTAL of PUBLICATION
        publication.setIdentifiant(messervices.generatePublicationId(
                (utilisateur.getNom() + " " + utilisateur.getPrenom()),
                publicationRepository.findAllByOrderByIdAsc().size(), 0));
        publication.setDateVoyage(
                OffsetDateTime.of(
                        LocalDateTime.ofEpochSecond((data.getMilliseconds() / 1000), 0
                                , ZoneOffset.UTC), ZoneOffset.UTC)
        );
        publication.setCreationDatetime(OffsetDateTime.now(Clock.systemUTC()));
        publicationRepository.save(publication);

        // Create temporary :
        final Publication pubTamp = publication;

        // Look for those who are interested :
        List<Cible> cibles = cibleRepository
                .findByVilleDepartAndVilleDestinationAndUtilisateurNot(villeDepart, villeDestination, utilisateur);
        if(!cibles.isEmpty()){
            // Process :
            String departDestination = villeDepart.getLibelle() + "  ->  " +
                    villeDestination.getLibelle();
            List<UserTokenMobileOs> lesCibles = cibles.stream()
                    .filter(cible ->
                        messervices.
                            checkNotificationRestriction(
                                cible.getUtilisateur(), pubTamp.getDateVoyage()))
                    .map(
                        cible -> messervices.generateObject(cible.getUtilisateur())
                    ).toList();
            if(!lesCibles.isEmpty()){
                firebasemessage.notifySuscriberAboutCible(lesCibles,
                        publication, departDestination, updatedPublication);
            }
        }

        //
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("id", publication.getId());
        stringMap.put("date", publication.getCreationDatetime().truncatedTo(ChronoUnit.SECONDS).
                format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        stringMap.put("identifiant", publication.getIdentifiant());
        stringMap.put("reserveBean", listeRefresh);
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/bookreservation")
    public ResponseEntity<?> bookreservation(
            @RequestBody ReservationRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Utilisateur suscriber = utilisateurRepository.findById(data.getIduser()).orElse(null);
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        //
        Reservation reservation = reservationRepository.
                findByUtilisateurAndPublication(suscriber, publication);
        // Create NEW OBJECT if needed :
        if(reservation == null) reservation = new Reservation();
        reservation.setReserve(data.getReserve());
        reservation.setMontant(data.getMontant());
        reservation.setUtilisateur(suscriber);
        reservation.setPublication(publication);
        reservation.setReservationState(ReservationState.EFFECTUE);
        reservation.setStreamChatId(""); // By DEFAULT
        // persist
        Reservation keepReservation = reservationRepository.save(reservation);

        // Return response :
        Map<String, Object> stringMap = new HashMap<>();
        Utilisateur owner = publication.getUtilisateur();
        stringMap.put("id", owner.getId());
        stringMap.put("nom", owner.getNom());
        stringMap.put("prenom", owner.getPrenom());
        stringMap.put("adresse", owner.getAdresse());
        Pays paysOwner = paysRepository.findById(owner.getPays().getId()).orElse(null);
        stringMap.put("nationnalite", paysOwner.getAbreviation());
        // Create CHANNEL ID, based on RESERVATION, SUSCRIBER, PUBLISHER :
        String channel_ID = keepReservation.getId().toString() +
                suscriber.getId().toString() +
                owner.getId().toString();
        // Update :
        keepReservation.setStreamChatId(channel_ID);
        reservationRepository.save(keepReservation);

        // Set CHANNEL ID :
        stringMap.put("channelid", channel_ID);

        // Add members :
        emailService.addMembersToChannels(Stream.of(suscriber, owner).toList(), channel_ID);

        // Notify the publication's OWNER
        Pays paysSuscriber = paysRepository.findById(suscriber.getPays().getId()).orElse(null);
        firebasemessage.notifyOwnerAboutNewReservation(owner,suscriber,reservation.getPublication(),
                paysSuscriber,
                reservation.getReserve(), channel_ID);

        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/generatewaveid")
    public ResponseEntity<?> generatewaveid(
            @RequestBody WavePaymentRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Utilisateur suscriber = utilisateurRepository.findById(data.getIduser()).orElse(null);
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        //
        Reservation reservation = reservationRepository.
                findByUtilisateurAndPublication(suscriber, publication);
        // CHECK if RESERVE is still available :
        assert publication != null;
        int totalReserve = publication.getReserve() - publication.getReservations().stream().mapToInt(Reservation::getReserve).sum();
        Map<String, Object> stringMap = new HashMap<>();
        if(data.getReserve() <= totalReserve) {
            // Create NEW OBJECT if needed :
            if (reservation == null) reservation = new Reservation();
            reservation.setReserve(data.getReserve());
            reservation.setMontant(data.getAmount());
            reservation.setUtilisateur(suscriber);
            reservation.setPublication(publication);
            reservation.setReservationState(ReservationState.EN_COURS);
            // persist
            reservationRepository.save(reservation);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + waveToken);
            headers.add("Content-Type", "application/json");

            try {
                // Call WEB Services :
                RestTemplate restTemplate = new RestTemplate();
                WavePaymentOriginalRequest objectRequest = new WavePaymentOriginalRequest();
                objectRequest.setAmount(data.getAmount());
                objectRequest.setCurrency(data.getCurrency());
                objectRequest.setErrorUrl(
                        backendWebUrl + "trobackend/invalidation/" +
                                reservation.getId());
                objectRequest.setSuccessUrl(
                        backendWebUrl + "trobackend/validation/" +
                                reservation.getId()
                );

                HttpEntity<WavePaymentOriginalRequest> entity = new HttpEntity<>(objectRequest, headers);
                ResponseEntity<WavePaymentResponse> responseEntity = restTemplate.postForEntity(waveUrl,
                        entity, WavePaymentResponse.class);
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    // Persist :
                    WavePaymentResponse wavePaymentResponse = responseEntity.getBody();
                    ApiRequest apiRequest = new ApiRequest();
                    apiRequest.setApiId(wavePaymentResponse.getId());
                    apiRequest.setLaunchUrl(wavePaymentResponse.getWaveLaunchUrl());
                    apiRequest.setReservation(reservation);
                    apiRequestRepository.save(apiRequest);

                    stringMap.put("id", wavePaymentResponse.getId());
                    stringMap.put("wave_launch_url", wavePaymentResponse.getWaveLaunchUrl());
                    stringMap.put("reserve", 0);
                    return ResponseEntity.ok(stringMap);
                }
            } catch (Exception exc) {
                System.out.println("Exception (generatewaveid) : " + exc.toString());
            }

            stringMap.put("id", "");
            stringMap.put("wave_launch_url", "");
            stringMap.put("reserve", 0);
        }
        else{
            stringMap.put("id", "");
            stringMap.put("wave_launch_url", "");
            stringMap.put("reserve", totalReserve);
        }
        return ResponseEntity.ok(stringMap);
    }


    @CrossOrigin("*")
    @PostMapping(path = "/managereservation")
    public ResponseEntity<?> managereservation(
            @RequestBody ReservationRequest data,
            HttpServletRequest request
    )
    {
        Map<String, Object> stringMap = new HashMap<>();
        // New LINE :
        Utilisateur suscriber = utilisateurRepository.findById(data.getIduser()).orElse(null);
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        // Find RESERVATION
        Reservation reservation = reservationRepository.
                findByUtilisateurAndPublication(suscriber, publication);
        if(reservation.getReservationState() == ReservationState.EFFECTUE) {
            // Return response :
            Utilisateur owner = publication.getUtilisateur();
            stringMap.put("id", owner.getId());
            stringMap.put("nom", owner.getNom());
            stringMap.put("prenom", owner.getPrenom());
            stringMap.put("adresse", owner.getAdresse());
            Pays paysOwner = paysRepository.findById(owner.getPays().getId()).orElse(null);
            stringMap.put("nationnalite", paysOwner.getAbreviation());

            String channel_ID = reservation.getId().toString() +
                    suscriber.getId().toString() +
                    owner.getId().toString();
            // Set CHANNEL ID :
            stringMap.put("channelid", channel_ID);

            return ResponseEntity.ok(stringMap);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
        }
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
        Utilisateur owner = publication.getUtilisateur();

        //
        Reservation reservation = reservationRepository.
                findByUtilisateurAndPublication(suscriber, publication);
        // Update :
        reservation.setReservationState(ReservationState.TRAITE);
        reservationRepository.save(reservation);
        // Notify to SUSCRIBER :
        firebasemessage.notifySuscriberAboutDelivery(suscriber, publication);
        // Notify BY EMAIL too :
        emailService.notificationLivraison(suscriber.getEmail(), (owner.getNom()+" "+owner.getPrenom()),
                String.valueOf(publication.getId()));

        // Compute :
        /*if(publication.getPrix() > 0) {
        }
        */

        return ResponseEntity.ok(Optional.empty());
    }

    @CrossOrigin("*")
    @PostMapping(path = "/markreceipt")
    public ResponseEntity<?> markreceipt(
            @RequestBody DeliveryRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Utilisateur suscriber = utilisateurRepository.findById(data.getIduser()).orElse(null);
        Publication publication = publicationRepository.findById(data.getIdpub()).orElse(null);
        Utilisateur owner = publication.getUtilisateur();
        //
        Reservation reservation = reservationRepository.
                findByUtilisateurAndPublication(suscriber, publication);

        // Process PRICE :
        if(publication.getPrix() > 0) {
            if (!suscriber.getCodeInvitation().isEmpty()) {
                // Look for OWNER GODFATHER :
                codeFiliationRepository.findByCode(suscriber.getCodeInvitation())
                        .ifPresent(
                                d -> {
                                    // Create new line in BONUS :
                                    Bonus bonus = new Bonus();
                                    bonus.setMontant((double) reservation.getMontant() * godfatherPayment);
                                    bonus.setUtilisateur(d.getUtilisateur());
                                    bonusRepository.save(bonus);
                                    // SUM UP bonuses
                                    List<Bonus> lesBonus =
                                            bonusRepository.findByUtilisateur(d.getUtilisateur());
                                    double totBonus = lesBonus.stream().mapToDouble(Bonus::getMontant).sum();
                                    // Notify GODFATHER :
                                    firebasemessage.notifyUserAboutBonus(d.getUtilisateur(),
                                            publication.getIdentifiant(),
                                            totBonus);

                                    // Payment to OWNER
                                    Bonus bonusOwner = new Bonus();
                                    bonusOwner.setMontant((double) reservation.getMontant() * ownerPaymentWithGodfather);
                                    bonusOwner.setUtilisateur(owner);
                                    bonusRepository.save(bonusOwner);
                                    // SUM UP bonuses
                                    List<Bonus> lesBonusOwner =
                                            bonusRepository.findByUtilisateur(owner);
                                    double totBonusOwner = lesBonusOwner.stream().mapToDouble(Bonus::getMontant).sum();
                                    // Notify OWNER :
                                    firebasemessage.notifyUserAboutBonus(owner,
                                            publication.getIdentifiant(),
                                            totBonusOwner);
                                }
                        );
            } else {
                // Payment to OWNER
                Bonus bonusOwner = new Bonus();
                bonusOwner.setMontant((double) reservation.getMontant() * ownerPaymentWithoutGodfather);
                bonusOwner.setUtilisateur(owner);
                bonusRepository.save(bonusOwner);
                // SUM UP bonuses
                List<Bonus> lesBonusOwner =
                        bonusRepository.findByUtilisateur(owner);
                double totBonusOwner = lesBonusOwner.stream().mapToDouble(Bonus::getMontant).sum();
                // Notify OWNER :
                firebasemessage.notifyUserAboutBonus(owner,
                        publication.getIdentifiant(),
                        totBonusOwner);
            }
        }

        // Update :
        reservation.setReservationState(ReservationState.RECU);
        reservationRepository.save(reservation);
        return ResponseEntity.ok(Optional.empty());
    }

    @CrossOrigin("*")
    @PostMapping(path = "/managenotification")
    public ResponseEntity<?> managenotification(
            @RequestBody NotificationRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Utilisateur user = utilisateurRepository.findById(data.getIduser()).orElse(null);
        assert user != null;
        NotificationsParam notificationsParam = user.getNotificationsParam();
        // Update :
        notificationsParam.setChoix(data.getChoix());
        if(data.getStartdatetime() > 0 && data.getEnddatetime() > 0){
            notificationsParam.setDebut(OffsetDateTime.of(
                    LocalDateTime.ofEpochSecond((data.getStartdatetime() / 1000), 0
                            , ZoneOffset.UTC), ZoneOffset.UTC));
            notificationsParam.setFin(OffsetDateTime.of(
                    LocalDateTime.ofEpochSecond((data.getEnddatetime() / 1000), 0
                            , ZoneOffset.UTC), ZoneOffset.UTC));
        }
        notificationsParamRepository.save(notificationsParam);
        return ResponseEntity.ok(Optional.empty());
    }


    @CrossOrigin("*")
    @PostMapping(path = "/sendaccusereception")
    public ResponseEntity<?> sendaccusereception(
            @RequestBody AccuseReceptionRequest data,
            HttpServletRequest request
    )
    {
        // New LINE :
        Chat chat = chatRepository.findByIdentifiant(data.getIdentifiant());
        if(chat != null){
            // Send FCM Notif :
            Utilisateur usr = utilisateurRepository.findById(
                    chat.getUtilisateurSender().getId()).orElse(null);
            if(usr != null) {
                firebasemessage.notifySenderAboutChatReceipt(usr,
                        data.getIdentifiant());
            }
        }
        return ResponseEntity.ok(Optional.empty());
    }

    //
    private boolean checkCodeParrainageExistence(String codeparrainage){
        Optional<CodeFiliation> cFiliation = codeFiliationRepository.findByCode(codeparrainage);
        return cFiliation.isPresent();
    }
}
