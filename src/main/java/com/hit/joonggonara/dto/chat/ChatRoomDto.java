package com.hit.joonggonara.dto.chat;

import com.hit.joonggonara.entity.Chat;

import java.util.List;

public record ChatRoomDto(
        Long roomId,
        String buyerNickName,
        String sellerNickName,
        List<Chat> chats
) {
    public static ChatRoomDto of(
            Long roomId,
            String buyerNickName,
            String sellerNickName,
            List<Chat> chats
    ){
        return new ChatRoomDto(roomId, buyerNickName, sellerNickName, chats);
    }
}
