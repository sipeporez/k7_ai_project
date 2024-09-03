package com.motorPM.config.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.motorPM.domain.Member;
import com.motorPM.persistence.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTAuthoFilter extends OncePerRequestFilter{
	private final MemberRepository mr;
	private final static String PREFIX = "Bearer ";
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String srcToken = request.getHeader("Authorization");
		if (srcToken == null || !srcToken.startsWith(PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String jwtToken = srcToken.replace(PREFIX, "");
		String username = JWT.require(Algorithm.HMAC256("com.predictivemaintenanceproject.backend"))
				.build()
				.verify(jwtToken)
				.getClaim("userid")
				.asString();
		Optional<Member> opt = mr.findByUserid(username);
		if (!opt.isPresent()) {
			filterChain.doFilter(request, response);
			return;
		}
		Member findmem = opt.get();
		User user = new User(
				findmem.getUserid(),
				findmem.getPassword(), 
				AuthorityUtils.createAuthorityList(findmem.getRole().toString()));
		
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		filterChain.doFilter(request, response);
		
	}

}
