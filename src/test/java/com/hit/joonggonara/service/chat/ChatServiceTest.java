package com.hit.joonggonara.service.chat;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ChatErrorCode;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.dto.request.chat.ChatRequest;
import com.hit.joonggonara.dto.request.chat.ChatRoomRequest;
import com.hit.joonggonara.dto.response.chat.ChatResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomAllResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomResponse;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.chat.ChatRepository;
import com.hit.joonggonara.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hit.joonggonara.common.type.ChatRoomStatus.BUYER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRepository chatRepository;
    @InjectMocks
    private ChatService sut;
    
    @Test
    @DisplayName("[Save][Chat] 채팅 기록이 정상적으로 저장 될 경우 true를 반환")
    void IfTheChatHistoryIsSavedNormallyReturnTrue() throws Exception
    {
        //given
        Long roomId = 1L;
        String buyerNickName = "buyerNickName";
        String sellerNickName = "sellerNickName";
        ChatRoom chatRoom = createChatRoom(buyerNickName, sellerNickName);
        ChatRequest chatRequest = createChatRequest();
        Chat chat = Chat.builder()
                .message("message")
                .createdMassageDate(LocalDateTime.now().toString())
                .chatRoom(chatRoom).build();
        given(chatRoomRepository.findById(any())).willReturn(Optional.of(chatRoom));
        given(chatRepository.save(any())).willReturn(chat);
        //when
        boolean expectedTrue = sut.saveChatHistory(roomId, chatRequest);
        //then
        assertThat(expectedTrue).isTrue();

        then(chatRoomRepository).should().findById(any());
        then(chatRepository).should().save(any());
    }
    @Test
    @DisplayName("[Delete][Chat] 채팅 삭제 요청이 오면 is_deleted를 true, message를 '삭제된 메세지입니다.' 로 변경 후 true를 반환")
    void deleteChatHistoryTest() throws Exception
    {
        //given
        Long chatId = 1L;
        //when
        boolean expectedTrue = sut.deleteChat(chatId);
        //then
        assertThat(expectedTrue).isTrue();
        then(chatRepository).should().deleteById(any());
    }

    @Test
    @DisplayName("[GetAll][Chat] 채팅 기록 전체 조회")
    void getAllChatHistoryTest() throws Exception
    {
        //given
        Long roomId = 1L;
        String senderNickName = "senderNickName";
        String recipientNickName = "recipientNickName";
        List<Chat> chats = createChats();
        given(chatRepository.findChatAllByRoomId(any())).willReturn(chats);
        //when
        List<ChatResponse> expectedChats = sut.getAllChats(roomId);
        //then
        assertThat(expectedChats.size()).isEqualTo(10);
        assertThat(expectedChats.get(0).senderNickName()).isEqualTo(senderNickName);
        assertThat(expectedChats.get(1).senderNickName()).isEqualTo(recipientNickName);
        assertThat(expectedChats.get(2).senderNickName()).isEqualTo(senderNickName);
        assertThat(expectedChats.get(3).senderNickName()).isEqualTo(recipientNickName);

        then(chatRepository).should().findChatAllByRoomId(any());

    }

    private List<Chat> createChats() throws InterruptedException {
        String senderNickName = "senderNickName";
        String recipientNickName = "recipientNickName";
        ChatRoom chatRoom = ChatRoom.builder().buyerNickName(senderNickName).sellerNickName(recipientNickName).build();
        List<Chat> chats = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Chat chat1 = Chat.builder()
                    .chatRoom(chatRoom)
                    .message("senderNickName" + i)
                    .senderNickName(senderNickName)
                    .createdMassageDate(LocalDateTime.now().toString())
                    .build();
            chats.add(chat1);
            Thread.sleep(1000);
            Chat chat2 = Chat.builder()
                    .chatRoom(chatRoom)
                    .message("recipientNickName" + i)
                    .senderNickName(recipientNickName)
                    .createdMassageDate(LocalDateTime.of(2024, 6, 15, 1, 1, 2).toString())
                    .build();
            chats.add(chat2);
        }
        return chats;
    }
    @Test
    @DisplayName("[Save][ChatRoom] 채팅방 저장 된 후 채팅방 정보를 true를 반환")
    void returnTrueWhenSaveChatRoom() throws Exception
    {
        //given
        ChatRoomRequest chatRoomRequest = createChatRoomRequest();
        ChatRoom chatRoom = ChatRoom.builder()
                .profile("profile")
                .sellerNickName("seller")
                .buyerNickName("buyer")
                .build();
        given(chatRoomRepository.save(any())).willReturn(chatRoom);
        //when
        ChatRoomResponse expectedResponse = sut.createRoom(chatRoomRequest);
        //then
        assertThat(expectedResponse.roomName()).isEqualTo("seller");
        assertThat(expectedResponse.nickName()).isEqualTo("buyer");
        assertThat(expectedResponse.profile()).isEqualTo("profile");
        then(chatRoomRepository).should().save(any());
    }


    @Test
    @DisplayName("[GetAll][Chat] nickName으로 데이터 베이스에 채팅방 조회 후 response 로 변환 후 반환")
    void findAllChatRoomSuccessTest() throws Exception
    {
        //given
        String buyerNickName = "buyerNickName";
        String sellerNickName = "sellerNickName";
        ChatRoom chatRoom = createChatRoom(buyerNickName, sellerNickName);
        List<Chat> chats = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            chats.add(createChat(chatRoom, i));
        }
        given(chatRoomRepository.findAllByNickName(any())).willReturn(List.of(chats.get(0).getChatRoom()));
        //when
        List<ChatRoomAllResponse> expectedChatRoomAllResponse = sut.getAllChatRoom(buyerNickName);
        //then
        assertThat(expectedChatRoomAllResponse.size()).isEqualTo(1);
        assertThat(expectedChatRoomAllResponse.get(0).roomName()).isEqualTo(sellerNickName);
        assertThat(expectedChatRoomAllResponse.get(0).message()).isEqualTo("message5");
        assertThat(expectedChatRoomAllResponse.get(0).chatRoomStatus()).isEqualTo(BUYER);

        then(chatRoomRepository).should().findAllByNickName(any());
    }

    // 1. buyer 삭제
    // 2. seller 삭제
    // 3. 채팅방이 존재하지 않을 때
    @Test
    @DisplayName("[Delete][ChatRoom] 채팅방이 정상적으로 삭제 될 경우 true를 리턴")
    void IfTheChatRoomIsDeletedNormallyReturnTrue() throws Exception
    {
        //given
        Long roomId = 1L;
        String buyerNickName = "buyerNickName";
        String sellerNickName = "sellerNickName";
        ChatRoom chatRoom = createChatRoom(buyerNickName, sellerNickName);
        given(chatRoomRepository.findById(any())).willReturn(Optional.of(chatRoom));
        //when
        boolean expectedTrue = sut.deleteChatRoom(roomId, BUYER);
        //then
        assertThat(expectedTrue).isTrue();
        then(chatRoomRepository).should().findById(any());
    }

    @Test
    @DisplayName("[Delete][ChatRoom] 채팅방이 존재하지 않으면 NOT_FOUND_CHATROOM를 던진다.")
    void ThrowNOT_FOUND_CHATROOMIfNotExistChatRoom() throws Exception
    {
        //given
        Long roomId = 1L;
        given(chatRoomRepository.findById(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.deleteChatRoom(roomId, BUYER));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(ChatErrorCode.NOT_FOUND_CHATROOM.getHttpStatus());
        assertThat(expectedException).hasMessage(ChatErrorCode.NOT_FOUND_CHATROOM.getMessage());
        then(chatRoomRepository).should().findById(any());
    }
    
    @Test
    @DisplayName("[Check Delete][ChatRoom] 빈 채팅방 삭제 테스트")
    void deleteEmptyChatRoomTest() throws Exception
    {
        //given
        Long roomId = 1L;
        //when
        boolean expectedTrue = sut.deleteEmptyChatRoom(roomId);
        //then
        assertThat(expectedTrue).isTrue();
        then(chatRoomRepository).should().deleteById(any());
    }


    private ChatRequest createChatRequest() {
        return ChatRequest.of("message","senderNickName");
    }
    private Chat createChat(ChatRoom chatRoom, int i) {
        return Chat.builder()
                .message("message" + i)
                .createdMassageDate(LocalDateTime.of(2024, 6, 15, 1,1,i).toString())
                .chatRoom(chatRoom)
                .build();
    }

    private ChatRoom createChatRoom(String buyerNickName, String sellerNickName) {
        return ChatRoom.builder().buyerNickName(buyerNickName).sellerNickName(sellerNickName).build();
    }

    private ChatRoomRequest createChatRoomRequest() {
        return ChatRoomRequest.of("profile", "buyer", "seller");
    }

    private Member createMember(String userId, String email, String name,String nickName, String password, String phoneNumber, LoginType loginType) {
        return Member.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .nickName(nickName)
                .password(password)
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_USER)
                .loginType(loginType)
                .build();
    }

}