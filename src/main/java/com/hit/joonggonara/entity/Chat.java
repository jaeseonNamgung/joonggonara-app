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

    @Id @GeneratedValue
    private Long id;

    private String message;
    private String image;
    private String createdMassageDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;


    @Builder
    public Chat(
            String message,
            String image,
            String createdMassageDate,
            ChatRoom chatRoom
    ) {
        this.message = message;
        this.image = image;
        this.createdMassageDate = createdMassageDate;
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
