package com.hit.joonggonara.service.chat;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ChatErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.type.ChatRoomStatus;
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
import com.hit.joonggonara.repository.login.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;


    // 채팅 기록 저장
    @Transactional
    public ChatResponse saveChatHistory(Long roomId, ChatRequest chatRequest){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ChatErrorCode.NOT_FOUND_CHATROOM));
        System.out.println(chatRequest.chatRoomStatus());
        if(chatRequest.chatRoomStatus().equals(ChatRoomStatus.BUYER.name()) && chatRoom.isSellerDeleted()){
            chatRoom.setSellerDeleted(false);
        } else if (chatRequest.chatRoomStatus().equals(ChatRoomStatus.SELLER.name()) && chatRoom.isBuyerDeleted()) {
            chatRoom.setBuyerDeleted(false);
        }
        return ChatResponse.fromResponse(chatRepository.save(chatRequest.toEntity(chatRoom)));
    }


    // 채팅 기록 삭제 (is_deleted를 true로 변경, messgage에 삭제된 메세지로 변경)
    @Transactional
    public boolean deleteChat(Long chatId){
        chatRepository.deleteById(chatId);
        return true;
    }

    // 채팅방 채팅 전체 조회 (정렬-> createMessageTime 내림차순)
    // roomId
    // response: roomId, senderNickName, message, createMessageTime
    public List<ChatResponse> getAllChats(Long roomId){
        List<Chat> chats = chatRepository.findChatAllByRoomId(roomId);
        return ChatResponse.fromResponse(chats);
    }

    // 채팅방 생성
    @Transactional
    public ChatRoomResponse createRoom(ChatRoomRequest chatRoomRequest){
        ChatRoom chatRoom = ChatRoom.builder()
                .profile(chatRoomRequest.profile())
                .buyerNickName(chatRoomRequest.buyerNickName())
                .sellerNickName(chatRoomRequest.sellerNickName())
                .build();

        return ChatRoomResponse.fromResponse(chatRoomRepository.save(chatRoom));
    }

    // 채팅방 전체 조회
    public List<ChatRoomAllResponse> getAllChatRoom(String nickName){
        List<ChatRoom> chatRoomDtoAllByNickName = chatRoomRepository.findAllByNickName(nickName);
        return chatRoomDtoAllByNickName.stream().map(chatRoom -> ChatRoomAllResponse.fromResponse(chatRoom, nickName))
                .collect(Collectors.toList());
    }

    // 빈 채팅방 삭제 여부
    public boolean deleteEmptyChatRoom(Long roomId){
        chatRoomRepository.deleteById(roomId);
        return true;
    }

    @Transactional
    public boolean deleteChatRoom(Long roomId, ChatRoomStatus chatRoomStatus){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ChatErrorCode.NOT_FOUND_CHATROOM));

        if(chatRoomStatus.equals(ChatRoomStatus.BUYER)){
            chatRoom.setBuyerDeleted(true);
        }else{
            chatRoom.setSellerDeleted(true);
        }
        return true;
    }







}
