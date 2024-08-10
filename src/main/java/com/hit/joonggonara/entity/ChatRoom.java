package com.hit.joonggonara.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ChatRoom extends BaseEntity{

    // chatRoomId
    @Id @GeneratedValue
    private Long id;

    private String profile;
    private String buyerNickName;
    private String sellerNickName;
    private boolean buyerDeleted;
    private boolean sellerDeleted;

    @OneToMany(mappedBy = "chatRoom")
    private List<Chat> chats = new ArrayList<>();

    @Builder
    public ChatRoom(String profile, String buyerNickName, String sellerNickName) {
        this.profile = profile;
        this.buyerNickName = buyerNickName;
        this.sellerNickName = sellerNickName;
        this.buyerDeleted = false;
        this.sellerDeleted = false;
    }

    public void setBuyerDeleted(Boolean deleteStatus){
        this.buyerDeleted = deleteStatus;
    }
    public void setSellerDeleted(Boolean deleteStatus){
        this.sellerDeleted = deleteStatus;
    }
}
