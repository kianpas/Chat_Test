package com.kh.upmu.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kh.upmu.model.vo.ChatMessage;
import com.kh.upmu.model.vo.MessageModel;
import com.kh.upmu.storage.UserStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MessageController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@MessageMapping("/TTTx")
	@SendTo("/topic/message")
	public String tttx(String message) throws Exception {
		log.debug("message = {}", message);
	
		//filew
		return message;
	}
	
	@MessageMapping("/TTT")
	@SendTo("/topic/message")
	public ChatMessage ttt(ChatMessage msg) throws Exception {
		log.debug("message = {}", msg);
		
		//filew
		return msg;
	}
//	@MessageMapping("/stompTest")
//	@SendTo("/topic/message")
//	public ChatMessage chat(ChatMessage msg) throws Exception {
//		log.debug("message = {}", msg);
//				System.out.println(msg);
//		simpMessagingTemplate.convertAndSend("/topic/message");
//		//filew
//		return msg;
//	}
	
	//엔드포인트 주소
	@MessageMapping("/chatxxx/{to}")
	public void sendMessage(@DestinationVariable String to, MessageModel message) {
		log.debug("message = {}  to = {}", message, to);
		boolean isExists = UserStorage.getInstance().getUsers().contains(to);
		if(isExists) {
			simpMessagingTemplate.convertAndSend("/topic/message/" + to, message);
		}
	}
	
	
	@GetMapping("/chatxx.do")
	public ModelAndView chat() {
		ModelAndView mav =new ModelAndView("page/hellochat");
		return mav;
	}
}
