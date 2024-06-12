package com.hit.joonggonara.dto.request.chat;

public record ChatRoomRequest(
        String buyerNickName,
        String sellerNickName
) {

    public static ChatRoomRequest of(
            String buyerNickName,
            String sellerNickName
    ){
        return new ChatRoomRequest(buyerNickName, sellerNickName);
    }
}
