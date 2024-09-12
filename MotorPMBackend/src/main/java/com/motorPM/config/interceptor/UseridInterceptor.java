package com.motorPM.config.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UseridInterceptor implements HandshakeInterceptor {

	private final MemberRepository mr;

	// 웹소켓 핸드셰이크 전에 실행되는 메서드
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		String userid = request.getURI().getQuery().substring(5); // 쿼리파라미터로 userid 추출, 'auth=' 잘라내기
		// 추출된 userid로 세션 생성
		if (mr.findByUserid(userid).isPresent()) {
			attributes.put("userid", userid);
			return true;
		}
		return false; // 실패시 핸드셰이크 중단
	}

	// 핸드셰이크 이후 실행되는 메서드(사용 안함)
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {

	}

}
