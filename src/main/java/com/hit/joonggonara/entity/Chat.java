package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String message;
    private String createdMassageDate;
    private String senderNickName;
    private boolean isFirstMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Builder
    public Chat(
            String message,
            String createdMassageDate,
            ChatRoom chatRoom,
            String senderNickName,
            boolean isFirstMessage
    ) {
        this.message = message;
        this.createdMassageDate = createdMassageDate;
        this.senderNickName = senderNickName;
        this.isFirstMessage = isFirstMessage;
        addChatRoom(chatRoom);
    }

    private void addChatRoom(ChatRoom chatRoom){
        if(this.chatRoom != null){
            this.chatRoom.getChats().remove(null);
        }
        this.chatRoom = chatRoom;
        chatRoom.getChats().add(this);
    }


}
