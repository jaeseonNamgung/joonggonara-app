package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
public class ChatRoom extends BaseEntity{

    // chatRoomId
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Member seller;

    @OneToMany(mappedBy = "chats")
    private List<Chat> chats = new ArrayList<>();

    @Builder
    public ChatRoom(Member buyer, Member seller) {
        this.buyer = buyer;
        this.seller = seller;
    }
}
