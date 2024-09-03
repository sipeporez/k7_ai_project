package com.motorPM.config.filter;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motorPM.domain.Member;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager am;
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Member member = mapper.readValue(request.getInputStream(), Member.class);
			Authentication authToken = new UsernamePasswordAuthenticationToken(
					member.getUserid(),
					member.getPassword());
			return am.authenticate(authToken);
			
		}catch (Exception e) {
			log.info(e.getMessage());
		}
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		return null;
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		User user = (User)authResult.getPrincipal();
		
		String token = JWT.create()
				.withClaim("userid", user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 600)) // 10시간	
				.sign(Algorithm.HMAC256("com.predictivemaintenanceproject.backend"));
		
		response.addHeader(HttpHeaders.AUTHORIZATION,"Bearer " +token);
		response.setStatus(HttpStatus.OK.value());
	}

}
