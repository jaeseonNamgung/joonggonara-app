package com.hit.joonggonara.repository.chat.querydsl;

import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.entity.QChatRoom;
import com.hit.joonggonara.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.hit.joonggonara.entity.QChat.chat;
import static com.hit.joonggonara.entity.QChatRoom.chatRoom;
import static com.hit.joonggonara.entity.QProduct.product;

@RequiredArgsConstructor
public class ChatRoomQueryDslImpl implements ChatRoomQueryDsl{


    private final JPAQueryFactory jpaQueryFactory;


    // buyer과 seller 에 속한 모든 채팅방을 조회하는데 삭제되지 않은 채팅방만 조회
    @Override
    public List<ChatRoom> findAllByNickName(String nickName) {
        QMember seller = new QMember("seller");
        QMember buyer = new QMember("buyer");

        return jpaQueryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.chats, chat).fetchJoin()
                .leftJoin(chatRoom.buyer, buyer).fetchJoin()
                .leftJoin(chatRoom.seller, seller).fetchJoin()
                .distinct()
                .where(findBuyerCondition(nickName).or(findSellerCondition(nickName)))
                .orderBy(chatRoom.createdDate.desc()).fetch();
    }

    @Override
    public Optional<ChatRoom> findChatInChatRoomAllByRoomId(Long roomId) {
        ChatRoom chatRoom = jpaQueryFactory.selectFrom(QChatRoom.chatRoom)
                .leftJoin(QChatRoom.chatRoom.chats).fetchJoin()
                .leftJoin(QChatRoom.chatRoom.product, product).fetchJoin()
                .where(QChatRoom.chatRoom.id.eq(roomId)).fetchOne();

        return Optional.ofNullable(chatRoom);
    }

    @Override
    public Optional<ChatRoom> findChatRoomByBuyerNickNameAndSellerNickNameAndProductId(String buyerNickName, String sellerNickName, Long productId) {
        ChatRoom chatRoom = jpaQueryFactory.selectFrom(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.buyer.nickName.eq(buyerNickName),
                        QChatRoom.chatRoom.seller.nickName.eq(sellerNickName),
                        QChatRoom.chatRoom.product.id.eq(productId)
                )
                .fetchOne();
        return Optional.ofNullable(chatRoom);
    }

    @Override
    public Optional<ChatRoom> findByRoomId(Long roomId) {
        ChatRoom chatRoom = jpaQueryFactory.selectFrom(QChatRoom.chatRoom)
                .leftJoin(QChatRoom.chatRoom.product, product).fetchJoin()
                .where(QChatRoom.chatRoom.id.eq(roomId)).fetchOne();
        return Optional.ofNullable(chatRoom);
    }


    BooleanExpression findBuyerCondition(String nickName){
        return chatRoom.buyer.nickName.eq(nickName).and(chatRoom.buyerDeleted.eq(false));
    }
    BooleanExpression findSellerCondition(String nickName){
        return chatRoom.seller.nickName.eq(nickName).and(chatRoom.sellerDeleted.eq(false));
    }


}
