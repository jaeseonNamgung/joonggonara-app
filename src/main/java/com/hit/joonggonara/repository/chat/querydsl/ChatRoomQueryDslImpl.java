package com.hit.joonggonara.repository.chat.querydsl;

import com.hit.joonggonara.dto.chat.ChatRoomDto;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.entity.QChat;
import com.hit.joonggonara.entity.QChatRoom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.hit.joonggonara.entity.QChat.chat;
import static com.hit.joonggonara.entity.QChatRoom.chatRoom;

@RequiredArgsConstructor
public class ChatRoomQueryDslImpl implements ChatRoomQueryDsl{


    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Optional<ChatRoomDto> findBuyerOrSellerByNickName(String nickName) {
        Chat lastChat = getLastMessage();
        jpaQueryFactory.select(
                Projections.constructor(ChatRoomDto.class,
                        chatRoom.id,
                        chatRoom.buyer.nickName,
                        chatRoom.seller.nickName,
                        lastChat.getMessage(),
                        lastChat.getCreatedDate().toString()
                        )
        )
    }

    private Chat getLastMessage() {
        QChat subChat = new QChat("chat");
        return JPAExpressions.selectFrom(chat)
                .where(chat.createdDate.eq(
                        JPAExpressions.select(chat.createdDate.max())
                )).fetchOne();
    }
}
