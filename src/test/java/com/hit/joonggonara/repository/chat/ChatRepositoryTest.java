package com.hit.joonggonara.repository.chat;

import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:/application.yaml")
@Import({JPAConfig.class, P6SpyConfig.class})
@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private ChatRepository sut;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("[JPA][QueryDsl] 채팅 삭제 테스트")
    void deleteChatTest() throws Exception
    {

        ChatRoom chatRoom = createChatRoom();
        ChatRoom savedChatRoom = em.merge(chatRoom);
        em.flush();
        em.clear();

        Chat chat = Chat.builder()
                .createdMassageDate(LocalDateTime.now().toString())
                .message("message")
                .chatRoom(savedChatRoom).build();
        Chat savedChat = sut.save(chat);
        assertThat(savedChat.isDeleted()).isFalse();
        sut.deleteById(savedChat.getId());
        em.flush();
        em.clear();
        Chat expectedChat = sut.findById(savedChat.getId()).get();
        assertThat(expectedChat.isDeleted()).isTrue();
        assertThat(expectedChat.getMessage()).isEqualTo("삭제된 메세지입니다.");
    }

    private ChatRoom createChatRoom() {
        return ChatRoom.builder().buyerNickName("buyerNickName").sellerNickName("sellerNickName").build();
    }

    @Test
    @DisplayName("[QueryDsl] roomId로 채팅 전체 조회")
    void findChatAllByRoomIdTest() throws Exception
    {
        //given
        ChatRoom chatRoom = createChatRoom();
        ChatRoom savedChatRoom1 = em.merge(chatRoom);
        ChatRoom savedChatRoom2 = em.merge(chatRoom);

        for (int i = 1; i <= 5; i++) {
            sut.save(createChat(i, savedChatRoom1));
            sut.save(createChat(i+5, savedChatRoom2));
        }
        //when
        List<Chat> expectedChats = sut.findChatAllByRoomId(savedChatRoom1.getId());
        //then
        assertThat(expectedChats.size()).isEqualTo(5);
        assertThat(expectedChats).extracting(Chat::getMessage).containsExactly( "message1", "message2", "message3", "message4", "message5");
    }

    private Chat createChat(int i, ChatRoom chatRoom) {
        return Chat.builder()
                .message("message" + i)
                .createdMassageDate(LocalDateTime.of(2024, 6, 18, 17, i).toString())
                .chatRoom(chatRoom)
                .build();
    }


}