package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.dto.request.chat.ChatRoomRequest;
import com.hit.joonggonara.dto.response.chat.ChatResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomAllResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomResponse;
import com.hit.joonggonara.service.chat.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.hit.joonggonara.common.type.ChatRoomStatus.BUYER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatApiController.class)
class ChatApiControllerTest {

    @MockBean
    private ChatService chatService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][POST] 채팅방 생성")
    void createChatRoomTest() throws Exception
    {
        //given
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.of("profile", "buyerNickName", "sellerNickName");
        ChatRoomResponse chatRoomResponse = createChatRoomResponse();
        given(chatService.createRoom(any())).willReturn(chatRoomResponse);
        //when & then
        mvc.perform(post("/chat/room/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRoomRequest))
                        .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roomName").value("seller"))
                .andExpect(jsonPath("$.nickName").value("buyer"))
                .andExpect(jsonPath("$.profile").value("profile"));

        then(chatService).should().createRoom(any());
    }



    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][Delete] 채팅 기록 삭제")
    void deleteChatHistoryTest() throws Exception
    {
        //given
        given(chatService.deleteChat(any())).willReturn(true);
        //when & then
        mvc.perform(delete("/chat/delete/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
        then(chatService).should().deleteChat(any());
    }

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][GET] 채팅 전체 조회")
    void getAllChatsTest() throws Exception
    {
        //given
        given(chatService.getAllChats(any())).willReturn(
                List.of(ChatResponse.of(1L, "message",
                        LocalDateTime.of(2024, 6, 20 ,  14, 50, 0).toString(),
                        "hong", false))
        );
        //when
        mvc.perform(get("/chat/all/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].chatId").value(1L))
                .andExpect(jsonPath("$[0].message").value("message"))
                .andExpect(jsonPath("$[0].createdMessageDate").value(LocalDateTime.of(2024, 6, 20 ,  14, 50, 0).toString()))
                .andExpect(jsonPath("$[0].senderNickName").value("hong"));
        //then
    }

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][GET] 채팅방 전체 조회")
    void getAllChatRoomTest() throws Exception
    {
        //given
        ChatRoomAllResponse chatRoomAllResponse;
        chatRoomAllResponse = ChatRoomAllResponse.of(1L, "profile", "message", LocalDateTime.now().toString(), "roomName", BUYER);
        given(chatService.getAllChatRoom(any())).willReturn(List.of(chatRoomAllResponse));
        //when & then
        mvc.perform(get("/chat/room")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("nickName", "buyerNickName")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].roomName").value("roomName"))
                .andExpect(jsonPath("$[0].message").value("message"))
                .andExpect(jsonPath("$[0].roomId").value(1L));
        then(chatService).should().getAllChatRoom(any());
    }

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][DELETE] 채팅방 삭제")
    void deleteChatRoomTest() throws Exception
    {
        //given
        given(chatService.deleteChatRoom(any(), any())).willReturn(true);
        //when & then
        mvc.perform(delete("/chat/room/delete/"+1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("chatRoomStatus", BUYER.name())
                        .with(csrf())
                ).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").value(true));
        then(chatService).should().deleteChatRoom(any(), any());
    }

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][DELETE] 빈 채팅방 삭제")
    void deleteEmptyChatRoomTest() throws Exception
    {
        //given
        given(chatService.deleteEmptyChatRoom(any())).willReturn(true);
        //when & then
        mvc.perform(delete("/chat/room/delete/empty/"+1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
        then(chatService).should().deleteEmptyChatRoom(any());
    }

    private ChatRoomResponse createChatRoomResponse() {
        return ChatRoomResponse.of(
                1L,
                "seller",
                "profile",
                "buyer"
        );
    }

}
