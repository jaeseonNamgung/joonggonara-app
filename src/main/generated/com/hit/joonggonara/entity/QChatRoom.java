package com.hit.joonggonara.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = 1755929269L;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final BooleanPath buyerDeleted = createBoolean("buyerDeleted");

    public final StringPath buyerNickName = createString("buyerNickName");

    public final ListPath<Chat, QChat> chats = this.<Chat, QChat>createList("chats", Chat.class, QChat.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath profile = createString("profile");

    public final BooleanPath sellerDeleted = createBoolean("sellerDeleted");

    public final StringPath sellerNickName = createString("sellerNickName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QChatRoom(String variable) {
        super(ChatRoom.class, forVariable(variable));
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatRoom(PathMetadata metadata) {
        super(ChatRoom.class, metadata);
    }

}

