package com.hit.joonggonara.repository.chat.querydsl;

import com.hit.joonggonara.entity.*;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.hit.joonggonara.entity.QChat.chat;
import static com.hit.joonggonara.entity.QChatRoom.chatRoom;
import static com.hit.joonggonara.entity.QMember.member;

@RequiredArgsConstructor
public class ChatQueryDslImpl implements ChatQueryDsl{

    private final JPAQueryFactory jpaQueryFactory;

}
