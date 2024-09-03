package com.motorPM.config.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JWTInterceptor implements HandshakeInterceptor {

	// 웹소켓 핸드셰이크 전에 실행되는 메서드
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// 쿼리스트링으로 JWT 토큰 추출
		String auth = request.getURI().getQuery().substring(5);
		// 추출된 JWT 토큰 디코딩
		if (auth != null && auth.startsWith("Bearer ")) {
			String jwtToken = auth.substring(7);
			String userid = JWT.require(
					Algorithm.HMAC256("com.predictivemaintenanceproject.backend"))
					.build()
					.verify(jwtToken)
					.getClaim("userid")
					.asString();
			if (userid != null) {
				// 인증된 사용자의 userid를 attributes에 추가
				attributes.put("userid", userid);
				return true;
			}
		}
		return false; // 실패시 핸드셰이크 중단
	}

	// 핸드셰이크 이후 실행되는 메서드(사용 안함)
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {

	}

}
