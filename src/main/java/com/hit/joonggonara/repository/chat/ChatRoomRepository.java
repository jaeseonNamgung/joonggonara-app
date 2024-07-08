package com.hit.joonggonara.repository.chat;

import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.repository.chat.querydsl.ChatRoomQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomQueryDsl{

}

