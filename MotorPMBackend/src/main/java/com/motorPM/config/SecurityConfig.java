package com.motorPM.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import com.motorPM.config.filter.JWTAuthenFilter;
import com.motorPM.config.filter.JWTAuthoFilter;
import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	
	private final MemberRepository mr;
	private final AuthenticationConfiguration authcon;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(ah-> ah
				.requestMatchers("/bookmark/**").authenticated()
				.requestMatchers("/charts/**").authenticated()
				.anyRequest().permitAll());
		
		http.httpBasic(b->b.disable());
		http.formLogin(f->f.disable());
		http.csrf(cs->cs.disable());
		http.cors(c->{}); 
		
		http.sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.addFilterBefore(new JWTAuthoFilter(mr), AuthorizationFilter.class);
		
		http.addFilter(new JWTAuthenFilter(authcon.getAuthenticationManager()));
		
		return http.build();
	}

}
