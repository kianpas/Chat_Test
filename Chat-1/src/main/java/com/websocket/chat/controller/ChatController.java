package com.websocket.chat.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.websocket.chat.model.vo.ChatMessage;
import com.websocket.chat.model.vo.ChatRoomRepository;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {
	
	private final SimpMessageSendingOperations messagingTemplate;
	
	@MessageMapping("/chat/message")
	public void message(ChatMessage message) {
		if(ChatMessage.MessageType.ENTER.equals(message.getType())) {
			message.setMessage(message.getSender() + "님이 입장하셨습니다.");
		}
		//websocket 메세지 redis로 발행
		
		messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
	}
	
}
