package com.hit.joonggonara.repository.chat;

import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/application.yaml")
@Import({JPAConfig.class, P6SpyConfig.class})
@DataJpaTest
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository sut;
    @Autowired
    private ChatRepository chatRepository;

    @Test
    @DisplayName("[QueryDsl][전제 조회] 채팅 방 전체 조회")
    void findAllChatRoomTest() throws Exception
    {
        //given
        String buyerNickName = "buyerNickName";
        String sellerNickName = "sellerNickName";
        ChatRoom chatRoom = ChatRoom.builder().buyerNickName(buyerNickName).sellerNickName(sellerNickName).build();
        ChatRoom savedChatRoom = sut.save(chatRoom);
        for (int i = 1; i <= 5; i++) {
            chatRepository.save(createChat(i,savedChatRoom));
        }
        //when
        List<ChatRoom> expectedChatRooms = sut.findAllByNickName(buyerNickName);
        //then
        for (ChatRoom expectedChatRoom : expectedChatRooms) {
            System.out.print("id: " + expectedChatRoom.getId()+ ", chatSize: " + expectedChatRoom.getChats().size());
            System.out.println();
        }
        assertThat(expectedChatRooms.size()).isEqualTo(1);
        assertThat(expectedChatRooms.get(0).getBuyerNickName()).isEqualTo(buyerNickName);
        assertThat(expectedChatRooms.get(0).getSellerNickName()).isEqualTo(sellerNickName);
        assertThat(expectedChatRooms.get(0).isBuyerDeleted()).isEqualTo(false);
        assertThat(expectedChatRooms.get(0).isSellerDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("[QueryDsl] 채팅방 채팅 기록 전체 조회")
    void findChatInChatRoomAllByRoomIdTest() throws Exception
    {
        //given
        String buyerNickName = "buyerNickName";
        String sellerNickName = "sellerNickName";
        ChatRoom chatRoom = ChatRoom.builder().buyerNickName(buyerNickName).sellerNickName(sellerNickName).build();
        ChatRoom savedChatRoom = sut.save(chatRoom);
        for (int i = 1; i <= 5; i++) {
            chatRepository.save(createChat(i,savedChatRoom));
        }
        //when
        ChatRoom expectedChatRoom = sut.findChatInChatRoomAllByRoomId(savedChatRoom.getId()).orElse(null);
        //then
        assertThat(expectedChatRoom).isNotNull();
        assertThat(expectedChatRoom.getId()).isEqualTo(savedChatRoom.getId());
        assertThat(expectedChatRoom.getChats().size()).isEqualTo(5);
    }


    private Chat createChat(int i, ChatRoom chatRoom) {
        return Chat.builder()
                .chatRoom(chatRoom)
                .message("message" + i)
                .createdMassageDate(LocalDateTime.of(2024, 6, 15, 1,1,i).toString())
                .build();
    }

    private Member createMember(String nickName) {
        return Member.builder()
                .userId("userId")
                .email("test@email.com")
                .name("hong")
                .nickName(nickName)
                .password("Abc1234*")
                .phoneNumber("+8612345678")
                .role(Role.ROLE_USER)
                .loginType(LoginType.GENERAL)
                .build();
    }

}
