package com.hit.joonggonara.repository.chat.querydsl;

import com.hit.joonggonara.entity.Chat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hit.joonggonara.entity.QChat.chat;


@RequiredArgsConstructor
public class ChatQueryDslImpl implements ChatQueryDsl{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Chat> findChatAllByRoomId(Long roomId) {
        return jpaQueryFactory.selectFrom(chat)
                .leftJoin(chat.chatRoom).fetchJoin()
                .where(chat.chatRoom.id.eq(roomId))
                .orderBy(chat.createdDate.asc())
                .fetch();
    }
}
