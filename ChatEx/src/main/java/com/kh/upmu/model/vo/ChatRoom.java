package com.kh.upmu.model.vo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ChatRoom{
	
	private String roomId;
	private String name;
	
	public static ChatRoom create(String name) {
		ChatRoom chatRoom = new ChatRoom();
		
		chatRoom.roomId = UUID.randomUUID().toString();
		chatRoom.name = name;
		
		return chatRoom;
	}
	
}
