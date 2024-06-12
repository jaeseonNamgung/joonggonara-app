package com.hit.joonggonara.service.chat;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ChatErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.dto.request.chat.ChatRequest;
import com.hit.joonggonara.dto.request.chat.ChatRoomRequest;
import com.hit.joonggonara.dto.response.chat.ChatResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomResponse;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.chat.ChatRepository;
import com.hit.joonggonara.repository.chat.ChatRoomRepository;
import com.hit.joonggonara.repository.login.MemberRepository;
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

import static com.hit.joonggonara.common.properties.JwtProperties.JWT_TYPE;
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
    private MemberRepository memberRepository;
    @InjectMocks
    private ChatService sut;


    @Test
    @DisplayName("[Service][Save] 채팅방 저장 된 후 채팅방 정보를 true를 반환")
    void returnTrueWhenSaveChatRoom() throws Exception
    {
        //given
        String senderNickName = "buyerNickName";
        String recipientNickName = "sellerNickName";
        ChatRoomRequest chatRoomRequest = createChatRoomRequest();
        Member sender = createMember(
                "senderId",
                "sender@email.com",
                "sender",
                senderNickName,
                "Abc1234*",
                "+8617512345678",
                LoginType.GENERAL);
        Member recipient = createMember(
                "recipientId",
                "recipient@email.com",
                "recipient",
                recipientNickName,
                "Abc1234*",
                "+8617512345678",
                LoginType.GENERAL);
        given(memberRepository.findBuyerByNickName(any())).willReturn(Optional.of(sender));
        given(memberRepository.findSellerByNickName(any())).willReturn(Optional.of(recipient));
        //when
        boolean expectedValue = sut.createRoom(chatRoomRequest);
        //then
        assertThat(expectedValue).isTrue();
        then(memberRepository).should().findBuyerByNickName(any());
        then(memberRepository).should().findSellerByNickName(any());
        then(chatRoomRepository).should().save(any());
    }

    @Test
    @DisplayName("[Service][Create] 발신자 유저가 존재하지 않을 경우 USER_NOT_FOUND 에러를 던진다.")
    void ThrowExceptionIfUserNotFound() throws Exception
    {
        //given
        ChatRoomRequest chatRoomRequest = createChatRoomRequest();
        given(memberRepository.findBuyerByNickName(any())).willReturn(Optional.empty());
        //when
       CustomException expectedException = (CustomException)catchException(()->sut.createRoom(chatRoomRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.USER_NOT_FOUND.getHttpStatus());
        assertThat(expectedException.getMessage()).isEqualTo(UserErrorCode.USER_NOT_FOUND.getMessage());
        then(memberRepository).should().findBuyerByNickName(any());
    }



    @Test
    @DisplayName("[Service][Create] 수신자 유저가 존재하지 않을 경우 RECIPIENT_NOT_FOUND 에러를 던진다.")
    void ThrowExceptionIfRecipientUserNotFound() throws Exception
    {
        //given
        ChatRoomRequest chatRoomRequest = createChatRoomRequest();
        Member sender = createMember(
                "senderId",
                "sender@email.com",
                "sender",
                "buyerNickName",
                "Abc1234*",
                "+8617512345678",
                LoginType.GENERAL);
        given(memberRepository.findBuyerByNickName(any())).willReturn(Optional.of(sender));
        given(memberRepository.findSellerByNickName(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException)catchException(()->sut.createRoom(chatRoomRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.RECIPIENT_NOT_FOUND.getHttpStatus());
        assertThat(expectedException.getMessage()).isEqualTo(UserErrorCode.RECIPIENT_NOT_FOUND.getMessage());
        then(memberRepository).should().findBuyerByNickName(any());
        then(memberRepository).should().findSellerByNickName(any());
    }

    private ChatRoomRequest createChatRoomRequest() {
        return ChatRoomRequest.of("buyer", "seller");
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