package com.ankk.tro.model;

import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.enums.SmartphoneType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(
    indexes = {
        @Index(name = "pays_utilisateur_id_idx", columnList = "pays_id"),
        @Index(name = "ville_utilisateur_id_idx", columnList = "ville_residence_id"),
        @Index(name = "type_piece_utilisateur_id_idx", columnList = "type_piece_id")
    }
)
@NoArgsConstructor
public class Utilisateur extends AbstractEntity{

    private String numeroPieceIdentite;
    private String nom;
    private String prenom;
    private String email;
    private String contact;
    private String adresse;
    private String pwd;
    private String fcmToken;
    private String streamChatToken;
    private String streamChatId;
    private String codeInvitation;
    private Integer active;
    private Integer validateAccount;
    @Enumerated(EnumType.ORDINAL)
    private SmartphoneType smartphoneType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pays_id", foreignKey = @ForeignKey(name = "FK_pays_utilisateur"))
    private Pays pays;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ville_residence_id", foreignKey = @ForeignKey(name = "FK_ville_utilisateur"))
    private Ville villeResidence;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "type_piece_id", foreignKey = @ForeignKey(name = "FK_type_piece_utilisateur"))
    private TypePiece typePiece;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateur")
    private Collection<Publication> publications;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateur")
    private Collection<Cible> cibles;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateur")
    private Collection<Reservation> reservations;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateurSender")
    private Collection<Chat> chatSender;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateurReceiver")
    private Collection<Chat> chatReceiver;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateur")
    private Collection<CodeFiliation> codeFiliations;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateur")
    private Collection<Bonus> bonuses;

    @OneToMany(fetch = LAZY, mappedBy = "utilisateur")
    private Collection<ReclamationPaiement> reclamationPaiements;

    @OneToOne
    @JoinColumn(name = "notification_param_id",  foreignKey = @ForeignKey(name = "FK_utilisateur_notification"))
    private NotificationsParam notificationsParam;
}
