package com.ankk.tro;

import com.ankk.tro.controller.ApiController;
import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.httpbean.BeanManageReservation;
import com.ankk.tro.httpbean.ReservationRequest;
import com.ankk.tro.model.Pays;
import com.ankk.tro.model.Publication;
import com.ankk.tro.model.Reservation;
import com.ankk.tro.model.Utilisateur;
import com.ankk.tro.repositories.PaysRepository;
import com.ankk.tro.repositories.PublicationRepository;
import com.ankk.tro.repositories.ReservationRepository;
import com.ankk.tro.repositories.UtilisateurRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = TroApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ManageReservationTest {

    // A T T R I B U T E S :
    @MockBean
    UtilisateurRepository utilisateurRepository;
    @MockBean
    PublicationRepository publicationRepository;
    @MockBean
    ReservationRepository reservationRepository;
    @MockBean
    PaysRepository paysRepository;
    @Autowired
    MockMvc mockMvc;


    // M e t h o d s :
    @Test
    public void createPartenaire() throws Exception{
        // Create FIRST USER :
        Utilisateur user1 = Utilisateur.builder()
                .id(1L)
                .build();
        Pays paysOwner = Pays.builder()
                .id(1L)
                .abreviation("CIV")
                .libelle("COTE D'IVOIRE")
                .build();
        Utilisateur owner = Utilisateur.builder()
                .id(2L)
                .nom("Konan")
                .prenom("Yao")
                .adresse("4 Rue de Charente")
                .pays(paysOwner)
                .build();
        // Feed :
        paysOwner.setUtilisateurs(Stream.of(owner).toList());
        Publication pub1 = Publication.builder()
                .id(1L)
                .utilisateur(owner)
                .build();
        Reservation reserv1 = Reservation.builder()
                .id(1L)
                .reservationState(ReservationState.EFFECTUE)
                .build();

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(pub1));
        when(reservationRepository.findByUtilisateurAndPublication(user1, pub1)).
                thenReturn(reserv1);
        when(paysRepository.findById(owner.getPays().getId())).
                thenReturn(Optional.of(paysOwner));

        ReservationRequest rt = new ReservationRequest();
        rt.setReserve(10);
        rt.setIdpub(1);
        rt.setIduser(1);
        rt.setMontant(100);

        BeanManageReservation bn = new BeanManageReservation();
        bn.setId(owner.getId());
        bn.setNom(owner.getNom());
        bn.setPrenom(owner.getPrenom());
        bn.setAdresse(owner.getAdresse());
        bn.setNationnalite(paysOwner.getAbreviation());
        // Set CHANNEL ID :
        bn.setChannelid("channel_ID");

        ObjectMapper objectMapper = new ObjectMapper();
        /*mockMvc.perform(post("/managereservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rt))
        ).andExpect(status().is2xxSuccessful());*/

        ResultActions resultActions = mockMvc.perform(post("/managereservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rt))
        );

        MvcResult mvcResult = resultActions.andExpect(status().is2xxSuccessful()).andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        BeanManageReservation article = objectMapper.readValue(json, BeanManageReservation.class);

        assertNotNull(article);
        //assertThat(mvcResult.getResponse().getContentAsByteArray()).isNotEmpty();
    }

}
