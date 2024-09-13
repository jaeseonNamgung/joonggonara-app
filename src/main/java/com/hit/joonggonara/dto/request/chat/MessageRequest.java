package com.hit.joonggonara.dto.request.chat;

public record MessageRequest(
        String roomId,
        String sender,
        String message
) {
}
