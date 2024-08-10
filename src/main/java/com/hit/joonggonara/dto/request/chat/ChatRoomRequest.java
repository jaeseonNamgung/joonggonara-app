package com.hit.joonggonara.dto.request.chat;

public record ChatRoomRequest(
        String profile,
        String buyerNickName,
        String sellerNickName
) {

    public static ChatRoomRequest of(
            String profile,
            String buyerNickName,
            String sellerNickName
    ){
        return new ChatRoomRequest(profile, buyerNickName, sellerNickName);
    }
}
