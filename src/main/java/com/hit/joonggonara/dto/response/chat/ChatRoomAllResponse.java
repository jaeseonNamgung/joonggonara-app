package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ChatErrorCode;
import com.hit.joonggonara.common.type.ChatRoomStatus;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record ChatRoomAllResponse(
        Long roomId,
        String profile,
        String message,
        String lastChatTime,
        String roomName,
        ChatRoomStatus chatRoomStatus

) {
    public static ChatRoomAllResponse of(
            Long roomId,
            String profile,
            String message,
            String lastChatTime,
            String roomName,
            ChatRoomStatus chatRoomStatus
    ){
        return new ChatRoomAllResponse(roomId,profile, message, lastChatTime,roomName, chatRoomStatus);
    }
    public static ChatRoomAllResponse fromResponse(ChatRoom chatRoom, String nickName){
        // 가장 최근 날짜 하나만 조회
        Chat chat = getMostRecentDate(chatRoom);
        // 채팅방 이름 조회 ( 채팅 상대방 닉네임 조회)
        String roomName = findRoomName(chatRoom, nickName);
        ChatRoomStatus chatRoomStatus = isChatRoomStatus(chatRoom, nickName);
        return ChatRoomAllResponse.of(
                chatRoom.getId(),
                chatRoom.getProfile(),
                chat.getMessage(),
                chat.getCreatedMassageDate(),
                roomName,
                chatRoomStatus
        );
    }
    private static ChatRoomStatus isChatRoomStatus(ChatRoom chatRoom, String nickName) {
        return chatRoom.getBuyerNickName().equals(nickName) ? ChatRoomStatus.BUYER : ChatRoomStatus.SELLER;
    }

    private static String findRoomName(ChatRoom chatRoom, String nickName) {
        if (chatRoom.getBuyerNickName().equals(nickName)){
            return chatRoom.getSellerNickName();
        }
        return chatRoom.getBuyerNickName();
    }

    private static Chat getMostRecentDate(ChatRoom chatRoom) {
        return chatRoom.getChats()
                .stream().sorted(Comparator.comparing(Chat::getCreatedMassageDate).reversed())
                .toList().get(0);
    }

}
