package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.type.ChatRoomStatus;
import com.hit.joonggonara.dto.request.chat.ChatRequest;
import com.hit.joonggonara.dto.request.chat.ChatRoomRequest;
import com.hit.joonggonara.dto.response.chat.ChatResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomAllResponse;
import com.hit.joonggonara.dto.response.chat.ChatRoomResponse;
import com.hit.joonggonara.dto.response.product.ProductResponse;
import com.hit.joonggonara.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@RequiredArgsConstructor
@RestController
public class ChatApiController {
    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;

    @MessageMapping("chat.message.{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    public ResponseEntity<ChatResponse> saveChat(@DestinationVariable("roomId") Long roomId, ChatRequest chatRequest){
        rabbitTemplate.convertAndSend("chat.exchange", "room."+roomId, chatRequest);
        return ResponseEntity.ok(chatService.saveChatHistory(roomId, chatRequest));
    }

    @GetMapping("/chat/all/{roomId}")
    public ResponseEntity<List<ChatResponse>> getAllChats(@PathVariable(name = "roomId") Long roomId){
        return ResponseEntity.ok(chatService.getAllChats(roomId));
    }
    @PostMapping("/chat/room/create/{productId}")
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody ChatRoomRequest chatRoomRequest,
                                                       @PathVariable(name = "productId")Long productId){
        return ResponseEntity.ok(chatService.createRoom(chatRoomRequest, productId));
    }

    @GetMapping("/chat/room")
    public ResponseEntity<List<ChatRoomAllResponse>> getAllChatRoom(@RequestParam(name = "nickName") String nickName){
        List<ChatRoomAllResponse> allChatRoom = chatService.getAllChatRoom(nickName);
        return ResponseEntity.ok(allChatRoom);
    }

    @DeleteMapping("/chat/room/delete/{roomId}")
    public ResponseEntity<Boolean> deleteChatRoom(
            @PathVariable(name = "roomId")Long roomId,
            @RequestParam(name = "chatRoomStatus") ChatRoomStatus chatRoomStatus){
        return ResponseEntity.ok(chatService.deleteChatRoom(roomId, chatRoomStatus));
    }

    @DeleteMapping("/chat/room/delete/empty/{roomId}")
    public ResponseEntity<Boolean> deleteEmptyChatRoom(
            @PathVariable(name = "roomId")Long roomId){
        return ResponseEntity.ok(chatService.deleteEmptyChatRoom(roomId));
    }

    @GetMapping("/chat/product/{roomId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable(name = "roomId") Long roomId){
        return ResponseEntity.ok(chatService.getProduct(roomId));
    }

}
