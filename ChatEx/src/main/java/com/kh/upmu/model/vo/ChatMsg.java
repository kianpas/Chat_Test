package com.kh.upmu.model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMsg {
	//메세지 타입 : 입장, 채팅
	public enum MessageType {
		ENTER, TALK
	}
	private MessageType type;
	private String roomId;
	private String sender;
	private String message;

}
