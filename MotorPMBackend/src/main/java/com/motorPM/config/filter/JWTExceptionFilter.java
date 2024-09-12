package com.motorPM.config.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.TokenExpiredException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		try {
			chain.doFilter(req, res); // JWTAuthenFilter 로 전달
		} catch (TokenExpiredException ex) {
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			res.setContentType("application/json; charset=UTF-8");
			res.getWriter().write("토큰 기한 만료됨. 재로그인 필요\n"+ex.getMessage());
		}
	}
}