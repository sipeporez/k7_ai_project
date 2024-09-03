package com.motorPM.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motorPM.domain.DTO.RealtimeDTO;
import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
@EnableWebSocket // Boot WebSocket 활성화
public class WebSocketConfig extends TextWebSocketHandler implements WebSocketConfigurer {
	
	private final MemberRepository mr;
    private List<Object[]> results;
    private int index;
    
 // 세션과 데이터를 매핑하기 위한 Map 추가
    private Map<WebSocketSession, List<Object[]>> sessionDataMap = new ConcurrentHashMap<>();

	// 연결된 클라이언트들을 저정하는 Set
	private static Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<WebSocketSession>());

	// WebSocket 연결명 설정 (http://localhost:8080/realtimews) ==> WebSocketConfigurer
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(this, "realtimews").setAllowedOrigins("*");
	}

	// Client가 접속 시 호출되는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		clients.add(session);
		System.out.println(session + " 클라이언트 접속");
	}

	// Client가 접속 해제 시 호출되는 메서드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	results = null;
    	index = 0;
		clients.remove(session);
		System.out.println(session + " 클라이언트 접속 해제");
	}
	
	// Client에서 메시지가 왔을 때 호출되는 메서드 ==> 채팅과 같은 형태의 기능을 추가하지 않는다면 필요없는 메소드이다.
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// uuid 길이가 아닌 경우 초기화
		if (message.getPayloadLength() != 36) {
	    	results = null;
	    	index = 0;
			return;
		}
		// asset_id를 메세지로 받은 경우 스케쥴러 작동(getResults)
		else {
			results = mr.realDataResult(message.getPayload());
			index = 0;
		}
	}
	
	// sendPushMessage를 0.781초 단위로 스케쥴링하여 호출하는 메서드
	@Scheduled(fixedRate = 781)
    public void getResults() {
    	if (results == null || results.isEmpty()) {
            return; // 결과가 없으면 작업 종료
        }
		if (index < results.size()) {
			Object[] result = results.get(index);
        		sendPushMessage(
        				RealtimeDTO.builder()
                		.asset_id(result[0].toString())
                		.created_at((Integer) result[1])
                		.spectrum_x((Float) result[2])
                		.spectrum_y((Float) result[3])
                		.spectrum_z((Float) result[4])
                		.build());
            index++;
        }
		else index = 0;
    }

	// FE에게 정보를 푸시하는 메소드
	public void sendPushMessage(RealtimeDTO data) {
		// 연결된 클라이언트가 없으면 그냥 리턴
		if (clients.size() == 0)
			return;

		// 자바 객체를 JSON 문자열로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		String msg;
		try {
			msg = objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			System.out.println("JSON Error:" + e.getMessage());
			return;
		}

		// FE에 전송할 JSON 메시지객체 생성
		TextMessage message = new TextMessage(msg);

		// 블럭안에 코드를 수행하는 동안 map 객체에 대한 다른 스레드의 접근을 방지한다.
		synchronized (clients) {
			for (WebSocketSession sess : clients) {
				try {
					sess.sendMessage(message);
				} catch (IOException e) {
					System.out.println(sess.getRemoteAddress() + ":" + e.getMessage());
				}
			}
		}
	}
}
