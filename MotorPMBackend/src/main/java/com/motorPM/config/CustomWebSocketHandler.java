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
import com.motorPM.domain.DTO.WaveDataArrayDTO;
import com.motorPM.domain.DTO.WaveDataDTO;
import com.motorPM.service.MainPageService;
import com.motorPM.service.WebSocketService;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CustomWebSocketHandler extends TextWebSocketHandler {
	// 세션을 사용자 이름으로 관리하기 위한 매핑
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(); // 사용자별 세션 관리
	private final Map<String, List<WaveDataDTO>> dataResults = new ConcurrentHashMap<>(); // 사용자별 결과 관리
	private final Map<String, WaveDataArrayDTO> arrayResults = new ConcurrentHashMap<>(); // 사용자별 결과 관리
	private final Map<String, Integer> userIndexes = new ConcurrentHashMap<>(); // 사용자별 인덱스 관리
	
	private final WebSocketService ws;
	private final MainPageService ms;

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
			// message = assetid, DYNAMIC(STATIC), wave(spec)
			String[] gubun = payload.split(", ");
			if (gubun[0].equalsIgnoreCase("STOP")) clearSession(userid);
			else {
				switch (gubun[1]) {
				case "DYNAMIC":
					clearSession(userid);
					List<WaveDataDTO> results = ws.getWaveData(gubun[0], gubun[2]); // 사용자별 결과 조회
					dataResults.put(userid, results);
					userIndexes.put(userid, 0);
					break;
				case "STATIC":
					clearSession(userid);
					WaveDataArrayDTO arr = ms.getWaveform(gubun[0], gubun[2]);
					arrayResults.put(userid, arr);
					userIndexes.put(userid, 0);
					break;
				}
			}
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
	@Scheduled(fixedRate = 100)
	public void getResults() {
		// 현재 연결된 모든 웹소켓을 순회
		for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
			String userid = entry.getKey();
			WebSocketSession session = entry.getValue();

			// 사용자가 데이터를 요청한 경우에만 처리
			if (dataResults.containsKey(userid)) { // gubunWaveSpec에 데이터가 없으면 건너뜀
				// WaveformDTO 처리 로직
				List<WaveDataDTO> results = dataResults.get(userid);
				
				Integer index = userIndexes.get(userid);

				if (results != null && !results.isEmpty() && index != null) {
					if (index >= results.size()) {
						closeSession(session, userid);
					} else {
						WaveDataDTO result = results.get(index);
						sendMessageToUser(session, result);
						userIndexes.put(userid, index + 1);
					}
				}
			}
			// 사용자가 데이터를 요청한 경우에만 처리
			else if (arrayResults.containsKey(userid)) { // gubunWaveSpec에 데이터가 없으면 건너뜀
				// WaveformDTO 처리 로직
				WaveDataArrayDTO results = arrayResults.get(userid);
				if (results != null) {
					sendMessageToUser(session, results);
					clearSession(userid);
					}
				}
			}
		}
	
	// 특정 사용자(세션)에게 JSON 형태의 메시지 보내기
	public void sendMessageToUser(WebSocketSession session, Object dto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			session.sendMessage(new TextMessage(mapper.writeValueAsString(dto)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 매핑 데이터 정리 메서드
	public void clearSession(String userid) {
		try {
			dataResults.remove(userid); // 사용자의 결과 제거
			arrayResults.remove(userid);
			userIndexes.remove(userid); // 사용자의 인덱스 제거
		}
		catch (Exception e) {
			System.out.println(userid + "클리어 중 에러 발생");
			e.printStackTrace();
		}
	}
	// 세션 종료 메서드
	public void closeSession(WebSocketSession session, String userid) {
		try {
			session.close(CloseStatus.NORMAL); // 정상적으로 세션을 종료
			sessions.remove(userid); // 세션 맵에서 제거
			clearSession(userid);
			System.out.println(userid + " 클라이언트 접속 해제");
		}
		catch (IOException e) {
			System.err.println(userid + "클라이언트 접속 해제 중 에러 발생");
			e.printStackTrace();
		}
	}
}
