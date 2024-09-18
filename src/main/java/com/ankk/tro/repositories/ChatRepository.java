package com.ankk.tro.repositories;

import com.ankk.tro.model.Chat;
import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends CrudRepository<Chat, Long> {
    Chat findByIdentifiant(String identifiant);
}
