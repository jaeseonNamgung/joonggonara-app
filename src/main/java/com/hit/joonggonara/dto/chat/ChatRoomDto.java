package com.hit.joonggonara.dto.chat;

public record ChatRoomDto(
        Long roomId,
        String buyerNickName,
        String sellerNickName,
        String lastMessage,
        String lastMessageAt
) {


}
