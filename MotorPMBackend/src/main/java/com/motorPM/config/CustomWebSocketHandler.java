package com.motorPM.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motorPM.domain.DTO.WaveformDTO;
import com.motorPM.service.WebSocketService;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CustomWebSocketHandler extends TextWebSocketHandler {
	// 세션을 사용자 이름으로 관리하기 위한 매핑
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(); // 사용자별 세션 관리
	private final Map<String, List<WaveformDTO>> userResults = new ConcurrentHashMap<>(); // 사용자별 결과 관리
	private final Map<String, Integer> userIndexes = new ConcurrentHashMap<>(); // 사용자별 인덱스 관리

	private final WebSocketService ws;

	// 클라이언트와 연결이 된 후에 실행되는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Map<String, Object> attributes = session.getAttributes();
		String userid = (String) attributes.get("userid");
		sessions.put(userid, session);
		System.out.println(userid + " 클라이언트 접속");
	}

	// 클라이언트로부터 메시지를 받았을때 실행되는 메서드
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 메시지 처리 로직
		String payload = message.getPayload();
		Map<String, Object> attributes = session.getAttributes();
		String userid = (String) attributes.get("userid");

		// 메시지를 받았을 때의 처리 예시
		if (userid != null) {
			System.out.println(userid + " 클라이언트로부터 받은 메시지 : " + payload);
			List<WaveformDTO> results = ws.getWaveform(payload); // 사용자별 결과 조회
			userResults.put(userid, results);
			userIndexes.put(userid, 0);
		}
	}

	// 연결이 종료된 후 실행되는 메서드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Map<String, Object> attributes = session.getAttributes();
		String userid = (String) attributes.get("userid");
		if (userid != null) closeSession(session, userid);
	}

	// sendPushMessage를 0.781초 단위로 스케쥴링하여 호출하는 메서드
	@Scheduled(fixedRate = 781)
	public void getResults() {
		// 현재 연결된 모든 웹소켓을 순회
		for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
			// 현재 사용자에 대한 userid와 session값을 불러옴
			String userid = entry.getKey();
			WebSocketSession session = entry.getValue();

			// 현재 사용자에 대한 sql query 결과와 index를 가져옴
			List<WaveformDTO> results = userResults.get(userid);
			Integer index = userIndexes.get(userid);

			// query 결과와 index가 null이 아니고 query 결과가 비어있지 않는 경우 실행
			if (results != null && !results.isEmpty() && index != null) {
				if (index >= results.size()) closeSession(session, userid); // 인덱스가 리스트의 크기를 초과하는 경우 세션을 종료
				else { // 인덱스가 리스트의 크기보다 작은 경우 results에 저장된 query 결과를 message로 전송
					WaveformDTO result = results.get(index);
					sendMessageToUser(session, result); // 각 사용자에게 개별 차트 데이터 전송
					userIndexes.put(userid, index + 1); // 스케쥴링 1회당 1번씩 출력 후 인덱스 증가
				}
			}
		}
	}

	// 특정 사용자(세션)에게 JSON 형태의 메시지 보내기
	public void sendMessageToUser(WebSocketSession session, WaveformDTO dto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			session.sendMessage(new TextMessage(mapper.writeValueAsString(dto)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 세션 종료 및 데이터 정리 메서드
	public void closeSession(WebSocketSession session, String userid) {
		try {
			session.close(CloseStatus.NORMAL); // 정상적으로 세션을 종료
			sessions.remove(userid); // 세션 맵에서 제거
			userResults.remove(userid); // 사용자의 결과 제거
			userIndexes.remove(userid); // 사용자의 인덱스 제거
			System.out.println(userid + " 클라이언트 접속 해제");
		}
		catch (IOException e) {
			System.err.println(userid + "클라이언트 접속 해제 중 에러 발생");
			e.printStackTrace();
		}
	}
}
