package com.motorPM.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.motorPM.config.interceptor.UseridInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket // Boot WebSocket 활성화
@RequiredArgsConstructor
public class WebSocketConfig extends TextWebSocketHandler implements WebSocketConfigurer {
	private final UseridInterceptor uidint;
	private final CustomWebSocketHandler csh;

	// WebSocket 연결명 설정 (ws://192.168.0.126:8080/realtimews) ==> WebSocketConfigurer
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(csh, "/realtimews") // 커스텀 웹소켓 핸들러로 /resltimews에 대한 웹소켓 연결 후 처리
		.setAllowedOrigins("*")	// CORS 설정
		.addInterceptors(uidint); // 쿼리파라미터로 사용자 id를 추출하여 개별 세션 설정
	}
}
