package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;

public record ChatRoomResponse(
        Long roomId,
        String roomName,
        String profile,
        String nickName
) {
    public static ChatRoomResponse of(
            Long roomId,
            String roomName,
            String profile,
            String nickName
    ){
        return new ChatRoomResponse(roomId, roomName, profile, nickName);
    }

    public static ChatRoomResponse fromResponse(ChatRoom chatRoom){
        return ChatRoomResponse.of(
                chatRoom.getId(),
                chatRoom.getSellerNickName(),
                chatRoom.getProfile(),
                chatRoom.getBuyerNickName());
    }

}
