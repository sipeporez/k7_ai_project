package com.motorPM.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
//		 registry.addMapping("/**")
//				 .allowedOriginPatterns(CorsConfiguration.ALL) // 모든 도메인에서의 요청 허용
//				 .allowedMethods(CorsConfiguration.ALL) // 모든 HTTP 메소드 허용
//				 .allowedHeaders(CorsConfiguration.ALL) // 모든 헤더를 허용
//				 .allowCredentials(true) // 쿠키와 같은 자격증명을 포함한 요청 허용
//				 .exposedHeaders(CorsConfiguration.ALL); // 클라이언트가 접근할 수 있는 응답 헤더
		 		
		registry.addMapping("/login")
		.allowCredentials(true)
		.exposedHeaders(HttpHeaders.AUTHORIZATION) 
		.allowedMethods(
				HttpMethod.POST.name()
				)
		.allowedOrigins(
				"http://localhost:8080",
				"http://localhost:3000",
				"http://127.0.0.1:3000",
				"http://192.168.0.126:3000",
				"http://192.168.0.144.nip.io:3000",
				"http://192.168.0.126.nip.io:3000",
				"http://192.168.0.144:3000"
				);
		
		registry.addMapping("/charts/**")
		.allowCredentials(true)
		.allowedHeaders(
				HttpHeaders.AUTHORIZATION,
				HttpHeaders.CONTENT_TYPE
				)
		.allowedMethods(
				HttpMethod.GET.name(),
				HttpMethod.POST.name()
				)
		.allowedOrigins(
				"http://localhost:8080",
				"http://localhost:3000",
				"http://127.0.0.1:3000",
				"http://192.168.0.126:3000",
				"http://192.168.0.144.nip.io:3000",
				"http://192.168.0.126.nip.io:3000",
				"http://192.168.0.144:3000"
				);
		
		registry.addMapping("/flask**")
		.allowCredentials(true)
		.allowedHeaders(
				HttpHeaders.CONTENT_TYPE
				)
		.allowedMethods(
				HttpMethod.GET.name()
				)
		.allowedOrigins(
				"http://localhost:8080",
				"http://localhost:3000",
				"http://127.0.0.1:3000",
				"http://192.168.0.126:3000",
				"http://192.168.0.144.nip.io:3000",
				"http://192.168.0.126.nip.io:3000",
				"http://192.168.0.144:3000"
				);
		
		registry.addMapping("/anomaly**")
		.allowCredentials(true)
		.allowedHeaders(
				HttpHeaders.AUTHORIZATION,
				HttpHeaders.CONTENT_TYPE
				)
		.allowedMethods(
				HttpMethod.GET.name()
				)
		.allowedOrigins(
				"http://localhost:8080",
				"http://localhost:3000",
				"http://127.0.0.1:3000",
				"http://192.168.0.126:3000",
				"http://192.168.0.144.nip.io:3000",
				"http://192.168.0.126.nip.io:3000",
				"http://192.168.0.144:3000"
				);
		
		registry.addMapping("/tempvolt")
		.allowCredentials(true)
		.allowedHeaders(
				HttpHeaders.AUTHORIZATION,
				HttpHeaders.CONTENT_TYPE
				)
		.allowedMethods(
				HttpMethod.POST.name()
				)
		.allowedOrigins(
				"http://localhost:8080",
				"http://localhost:3000",
				"http://127.0.0.1:3000",
				"http://192.168.0.126:3000",
				"http://192.168.0.144.nip.io:3000",
				"http://192.168.0.126.nip.io:3000",
				"http://192.168.0.144:3000"
				);
		
		registry.addMapping("/waveform")
		.allowCredentials(true)
		.allowedHeaders(
				HttpHeaders.AUTHORIZATION,
				HttpHeaders.CONTENT_TYPE
				)
		.allowedMethods(
				HttpMethod.POST.name()
				)
		.allowedOrigins(
				"http://localhost:8080",
				"http://localhost:3000",
				"http://127.0.0.1:3000",
				"http://192.168.0.126:3000",
				"http://192.168.0.144.nip.io:3000",
				"http://192.168.0.126.nip.io:3000",
				"http://192.168.0.144:3000"
				);
		
	}
}
