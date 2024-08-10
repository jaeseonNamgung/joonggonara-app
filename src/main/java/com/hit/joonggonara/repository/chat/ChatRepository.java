package com.hit.joonggonara.repository.chat;

import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.repository.chat.querydsl.ChatQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> , ChatQueryDsl {

}
