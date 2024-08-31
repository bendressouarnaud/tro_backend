package com.ankk.tro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(
        indexes = {
                @Index(name = "utilisateur_sender_chat_id_idx", columnList = "utilisateur_sender_id"),
                @Index(name = "utilisateur_receiver_chat_id_idx", columnList = "utilisateur_receiver_id"),
                @Index(name = "publication_chat_id_idx", columnList = "publication_id")
        }
)
@NoArgsConstructor
public class Chat extends AbstractEntity{

    private String message;
    private String identifiant;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_sender_id", foreignKey = @ForeignKey(name = "FK_utilisateur_sender_chat"))
    private Utilisateur utilisateurSender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "utilisateur_receiver_id", foreignKey = @ForeignKey(name = "FK_utilisateur_receiver_chat"))
    private Utilisateur utilisateurReceiver;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "publication_id", foreignKey = @ForeignKey(name = "FK_publication_chat"))
    private Publication publication;
}
