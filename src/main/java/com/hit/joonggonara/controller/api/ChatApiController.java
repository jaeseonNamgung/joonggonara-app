package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.dto.request.chat.ChatRequest;
import com.hit.joonggonara.dto.request.chat.ChatRoomRequest;
import com.hit.joonggonara.dto.response.chat.ChatResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomResponse;
import com.hit.joonggonara.service.chat.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatApiController {

    private final SimpMessageSendingOperations operations;
    private final ChatService chatService;

    @PostMapping("/chat/room")
    public ResponseEntity<Boolean> createRoom(@RequestBody ChatRoomRequest chatRoomRequest){
        return ResponseEntity.ok(chatService.createRoom(chatRoomRequest));
    }

//    @GetMapping("/chat/room")
//    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(
//            @Header("Authorization") String authHeader
//    ){
//        return ResponseEntity.ok(chatService.getChatRooms(authHeader));
//    }

    @DeleteMapping("/chat/room/{roomId}")
    public ResponseEntity<Boolean> checkChatHistory(@PathVariable(name = "roomId") Long roomId){
        return ResponseEntity.ok(chatService.checkChatHistory(roomId));
    }

    @DeleteMapping("/chat/room/delete/{roomId}")
    public ResponseEntity<Boolean> deleteChatRoom(@PathVariable(name = "roomId") Long roomId){
        return ResponseEntity.ok(chatService.deleteChatRoom(roomId));
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<ChatResponse>> getAllChat(@PathVariable("roomId") Long roomId){
        return ResponseEntity.ok(chatService.getChats(roomId));
    }

    @MessageMapping("/chat/{roomId}")
    public ResponseEntity<Boolean> chat(
            @DestinationVariable("roomId") Long roomId,
            @RequestBody ChatRequest chatRequest
            ){
        boolean isTrue = chatService.saveMessage(roomId, chatRequest);
        operations.convertAndSend("/sub/chat/" + roomId, chatRequest);
        return ResponseEntity.ok(isTrue);
    }


}
