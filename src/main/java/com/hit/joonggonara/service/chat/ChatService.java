package com.hit.joonggonara.service.chat;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ChatErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.dto.request.chat.ChatRequest;
import com.hit.joonggonara.dto.request.chat.ChatRoomRequest;
import com.hit.joonggonara.dto.response.chat.ChatResponse;
import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.chat.ChatRepository;
import com.hit.joonggonara.repository.chat.ChatRoomRepository;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.hit.joonggonara.repository.login.condition.LoginCondition;
import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.hit.joonggonara.common.properties.JwtProperties.JWT_TYPE;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;


    // 채팅방 생성
    @Transactional
    public boolean createRoom(ChatRoomRequest chatRoomRequest){
        Member buyer = memberRepository.findBuyerByNickName(chatRoomRequest.buyerNickName())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Member seller = memberRepository.findSellerByNickName(chatRoomRequest.sellerNickName())
                .orElseThrow(() -> new CustomException(UserErrorCode.RECIPIENT_NOT_FOUND));
        ChatRoom chatRoom = new ChatRoom(buyer, seller);
        chatRoomRepository.save(chatRoom);
        return true;
    }


}
