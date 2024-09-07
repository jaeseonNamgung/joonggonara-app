package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.dto.response.product.ProductResponse;
import com.hit.joonggonara.entity.ChatRoom;

public record ChatRoomResponse(
        Long roomId,
        String roomName,
        String profile,
        String nickName,
        ProductResponse productResponse
) {
    public static ChatRoomResponse of(
            Long roomId,
            String roomName,
            String profile,
            String nickName,
            ProductResponse productResponse
    ){
        return new ChatRoomResponse(roomId, roomName, profile, nickName, productResponse);
    }

    public static ChatRoomResponse fromResponse(ChatRoom chatRoom){
        return ChatRoomResponse.of(
                chatRoom.getId(),
                chatRoom.getSeller().getNickName(),
                chatRoom.getSeller().getProfile(),
                chatRoom.getBuyer().getNickName(),
                ProductResponse.fromResponse(chatRoom.getProduct()));
    }

}
