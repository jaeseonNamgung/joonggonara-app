package com.hit.joonggonara.repository.chat.querydsl;

import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.entity.QChatRoom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.hit.joonggonara.entity.QChatRoom.chatRoom;

@RequiredArgsConstructor
public class ChatRoomQueryDslImpl implements ChatRoomQueryDsl{


    private final JPAQueryFactory jpaQueryFactory;


    // buyer과 seller 에 속한 모든 채팅방을 조회하는데 삭제되지 않은 채팅방만 조회
    @Override
    public List<ChatRoom> findAllByNickName(String nickName) {
        return jpaQueryFactory
                .selectFrom(chatRoom)
                .join(chatRoom.chats).fetchJoin()
                .where(
                        findBuyerCondition(nickName).or(findSellerCondition(nickName))
                ).orderBy(chatRoom.createdDate.desc()).fetch();
    }

    @Override
    public Optional<ChatRoom> findChatInChatRoomAllByRoomId(Long roomId) {
        ChatRoom chatRoom = jpaQueryFactory.selectFrom(QChatRoom.chatRoom)
                .join(QChatRoom.chatRoom.chats).fetchJoin()
                .where(QChatRoom.chatRoom.id.eq(roomId)).fetchOne();

        return Optional.ofNullable(chatRoom);
    }

    @Override
    public Optional<ChatRoom> findChatRoomByBuyerNickNameAndSellerNickName(String buyerNickName, String sellerNickName) {
        ChatRoom chatRoom = jpaQueryFactory.selectFrom(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.buyerNickName.eq(buyerNickName), QChatRoom.chatRoom.sellerNickName.eq(sellerNickName))
                .fetchOne();
        return Optional.ofNullable(chatRoom);
    }

    BooleanExpression findBuyerCondition(String nickName){
        return chatRoom.buyerNickName.eq(nickName).and(chatRoom.buyerDeleted.eq(false));
    }
    BooleanExpression findSellerCondition(String nickName){
        return chatRoom.sellerNickName.eq(nickName).and(chatRoom.sellerDeleted.eq(false));
    }


}
