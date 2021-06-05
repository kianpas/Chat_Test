package com.psw.chatting.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Component
public class SocketHandler extends TextWebSocketHandler {
	
	private static final Logger log = LoggerFactory.getLogger(SocketHandler.class);
	
	//HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); // 웹소켓 세션을 담아둘 맵
	List<HashMap<String, Object>> rls = new ArrayList<>(); //웹소켓 세션을 담아둘 리스트 ---roomListSessions
	private static final String FILE_UPLOAD_PATH = "C:/test/websocket/";
	static int fileUploadIdx = 0;
	static String fileUploadSession = "";
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		// 메시지 발송
		String msg = message.getPayload();
		JsonObject obj = jsonToObjectParser(msg);
		
		log.info("msg = {}", msg);
		log.info("obj = {}", obj);
		String rN = obj.get("roomNumber").toString();
		String er = obj.get("msg").toString();
		//log.info("onlymsg = {}", er);
		
		String msgType = obj.get("type").toString(); //메시지의 타입을 확인한다.
		
		HashMap<String, Object> temp = new HashMap<String, Object>();
		
		//log.info("rls = {}", rls);
		//log.info("rlsrls.size() = {}", rls.size());
		if(rls.size() > 0) {
			for(int i=0; i<rls.size(); i++) {
				String roomNumber = (String) rls.get(i).get("roomNumber"); //세션리스트의 저장된 방번호를 가져와서
				//log.info("1 = {}", roomNumber);
				//log.info("rN = {}", rN);
				log.info(rN=roomNumber);
				if(roomNumber.equals(rN)) { //같은값의 방이 존재한다면
					//log.info("2 = {}", roomNumber);
					//log.info("3 = {}", rN);
					temp = rls.get(i); //해당 방번호의 세션리스트의 존재하는 모든 object값을 가져온다.
					//파일관련
					fileUploadIdx=i;
					fileUploadSession = obj.get("sessionId").toString();
					break;
				}
			}
			
			if(!msgType.equals("fileUpload")) {
			//해당 방의 세션들만 찾아서 메시지를 발송해준다.
			for(String k : temp.keySet()) { 
				if(k.equals("roomNumber")) { //다만 방번호일 경우에는 건너뛴다.
					//log.info(k);
					
					continue;
				}
				log.info(k);
				WebSocketSession wss = (WebSocketSession) temp.get(k);
				if(wss != null) {
					try {
						//log.info("obj.toString() = {}", obj.toString());
						wss.sendMessage(new TextMessage(obj.toString()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			}
		}
		
//		for (String key : sessionMap.keySet()) {
//			WebSocketSession wss = sessionMap.get(key);
//			try {
//				wss.sendMessage(new TextMessage(obj.toString()));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 소켓 연결
		super.afterConnectionEstablished(session);
		//sessionMap.put(session.getId(), session);
		
		boolean flag = false;
		String url = session.getUri().toString();
		//log.info(url);
		String roomNumber = url.split("/chatting/")[1];
		
		int idx = rls.size(); //방의 사이즈를 조사한다.
		if(rls.size() > 0) {
			for(int i=0; i<rls.size(); i++) {
				String rN = (String) rls.get(i).get("roomNumber");
				if(rN.equals(roomNumber)) {
					flag = true;
					idx = i;
					break;
				}
			}
		}
		
		if(flag) { //존재하는 방이라면 세션만 추가한다.
			HashMap<String, Object> map = rls.get(idx);
			map.put(session.getId(), session);
			//log.info(map.toString());
		}else { //최초 생성하는 방이라면 방번호와 세션을 추가한다.
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roomNumber", roomNumber);
			map.put(session.getId(), session);
			//log.info("map = {}", map.toString());
			rls.add(map);
			//log.info("rls = {}", rls);
		}
		
		JsonObject obj = new JsonObject();
		obj.add("type", JsonParser.parseString("getId"));
		obj.add("sessionId", JsonParser.parseString(session.getId()));
		//log.info("obj = {}", obj);
		session.sendMessage(new TextMessage(obj.toString()));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//소켓 종료
				if(rls.size() > 0) { //소켓이 종료되면 해당 세션값들을 찾아서 지운다.
					for(int i=0; i<rls.size(); i++) {
						rls.get(i).remove(session.getId());
					}
				}
		super.afterConnectionClosed(session, status);
	}
	
	private static JsonObject jsonToObjectParser(String jsonStr) {
		
		JsonObject obj = null;
		obj = JsonParser.parseString(jsonStr).getAsJsonObject();
	
		return obj;
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		//바이너리
		ByteBuffer byteBuffer = message.getPayload();
		log.info("btyeBuffer = {}", byteBuffer);
		String fileName= "temp.jpg";
		File dir = new File(FILE_UPLOAD_PATH);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(FILE_UPLOAD_PATH, fileName);
		FileOutputStream out= null;
		FileChannel outChannel =null;
		log.info("file = {}", file);
		try {
			byteBuffer.flip();
			out = new FileOutputStream(file, true);
			outChannel = out.getChannel();
			byteBuffer.compact();
			outChannel.write(byteBuffer);
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if(out!=null) {
					out.close();
				}
				if(outChannel !=null) {
					outChannel.close();
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		byteBuffer.position(0);
		HashMap<String, Object>temp = rls.get(fileUploadIdx);
		for(String k : temp.keySet()) {
			if(k.equals("roomNumber")) {
				continue;
			}
			WebSocketSession wss = (WebSocketSession)temp.get(k);
			try {
				wss.sendMessage(new BinaryMessage(byteBuffer));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		super.handleBinaryMessage(session, message);
	}
}	
