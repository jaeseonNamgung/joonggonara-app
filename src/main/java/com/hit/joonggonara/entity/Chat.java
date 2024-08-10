package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;


@SQLDelete(sql = "UPDATE chat SET is_deleted = true, message = '삭제된 메세지입니다.' WHERE id = ?")
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
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Builder
    public Chat(
            String message,
            String createdMassageDate,
            ChatRoom chatRoom,
            String senderNickName
    ) {
        this.message = message;
        this.createdMassageDate = createdMassageDate;
        this.senderNickName = senderNickName;
        this.isDeleted = false;
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
