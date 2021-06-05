package com.kh.upmu.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.upmu.model.vo.ChatRoom;
import com.kh.upmu.model.vo.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//구독자 구현
@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/chat")
public class ChatRoomController {
	
	private final ChatRoomRepository chatRoomRepository;
	
	//채팅리스트 화면
	@GetMapping("/room")
	public String rooms(Model model) {
		return "/chat/room";
	}
	
	//모든 채팅방 목록 반환
	@GetMapping("/rooms")
	@ResponseBody
	public List<ChatRoom> room(){
		return chatRoomRepository.findAllRoom();
	}
	
	//채팅방 생성
	@PostMapping("/room")
	@ResponseBody
	public ChatRoom createRoom(@RequestParam String name) {
		log.debug("name {}", name);
		return chatRoomRepository.createChatRoom(name);
	}
	
	//채팅방 입장화면
	@GetMapping("/room/enter/{roomId}")
	public String roomDetail(Model model, @PathVariable String roomId) {
		model.addAttribute("roomId", roomId);
		return "/chat/roomdetail";
	}
	
	//특정 채팅방조회
	@GetMapping("/room/{roomId}")
	@ResponseBody
	public ChatRoom roomInfo(@PathVariable String roomId) {
		return chatRoomRepository.findRoomById(roomId);
	}
}