package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.common.type.ChatRoomStatus;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;

import java.util.Comparator;

public record ChatRoomAllResponse(
        Long roomId,
        String profile,
        String message,
        String lastChatTime,
        String roomName,
        boolean withdrawalStatus,
        ChatRoomStatus chatRoomStatus
) {
    public static ChatRoomAllResponse of(
            Long roomId,
            String profile,
            String message,
            String lastChatTime,
            String roomName,
            boolean withdrawalStatus,
            ChatRoomStatus chatRoomStatus
    ){
        return new ChatRoomAllResponse(roomId,profile, message, lastChatTime,roomName,withdrawalStatus, chatRoomStatus);
    }
    public static ChatRoomAllResponse fromResponse(ChatRoom chatRoom, String nickName){
        if(chatRoom.getChats().isEmpty()) return null;
        // 가장 최근 날짜 하나만 조회
        Chat chat = getMostRecentDate(chatRoom);
        // 채팅방 이름 조회 ( 채팅 상대방 닉네임 조회)
        String roomName = findRoomName(chatRoom, nickName);
        ChatRoomStatus chatRoomStatus = isChatRoomStatus(chatRoom, nickName);
        // 수신 측 프로필 사진 가져오기
        String profile = findProfile(chatRoom, nickName);

        boolean withdrawalStatus = checkWithdrawalStatus(chatRoom, nickName);

        return ChatRoomAllResponse.of(
                chatRoom.getId(),
                profile,
                chat.getMessage(),
                chat.getCreatedMassageDate(),
                roomName,
                withdrawalStatus,
                chatRoomStatus
        );
    }

    private static boolean checkWithdrawalStatus(ChatRoom chatRoom, String nickName) {
        if(chatRoom.getBuyer().getNickName().equals(nickName)){
            return chatRoom.getSeller().isDeleted();
        }

        return chatRoom.getBuyer().isDeleted();
    }

    private static String findProfile(ChatRoom chatRoom, String nickName) {
        return chatRoom.getBuyer().getNickName().equals(nickName)?
                chatRoom.getSeller().getProfile() : chatRoom.getBuyer().getProfile();
    }

    private static ChatRoomStatus isChatRoomStatus(ChatRoom chatRoom, String nickName) {
        return chatRoom.getBuyer().getNickName().equals(nickName)? ChatRoomStatus.BUYER : ChatRoomStatus.SELLER;
    }

    private static String findRoomName(ChatRoom chatRoom, String nickName) {
        if (chatRoom.getBuyer().getNickName().equals(nickName)){
            return chatRoom.getSeller().getNickName();
        }
        return chatRoom.getBuyer().getNickName();
    }

    private static Chat getMostRecentDate(ChatRoom chatRoom) {
        return chatRoom.getChats()
                .stream().sorted(Comparator.comparing(Chat::getCreatedMassageDate).reversed())
                .toList().get(0);
    }

}
