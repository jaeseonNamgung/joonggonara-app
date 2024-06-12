package com.hit.joonggonara.repository.chat.querydsl;

import com.hit.joonggonara.dto.chat.ChatRoomDto;

import java.util.Optional;

public interface ChatRoomQueryDsl {

    Optional<ChatRoomDto> findBuyerOrSellerByNickName(String nickName);
}
