package com.motorPM.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordConfig {

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
		
	}
}
