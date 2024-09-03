//package com.motorPM;
//
//import java.time.LocalDateTime;
//
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import com.motorPM.domain.Member;
//import com.motorPM.domain.Role;
//import com.motorPM.persistence.MemberRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class MemberInit implements ApplicationRunner {
//	private final PasswordEncoder enc;
//	private final MemberRepository mr;
//	
//	@Override
//	public void run(ApplicationArguments args) throws Exception {
//		try {
//			mr.save(Member.builder()
//					.userid("test")
//					.username("테스트유저1")
//					.password(enc.encode("11"))
//					.login_date(LocalDateTime.now())
//					.phone("01012340000")
//					.build());
//			mr.save(Member.builder()
//					.userid("admin")
//					.username("어드민")
//					.password(enc.encode("11"))
//					.login_date(LocalDateTime.now())
//					.role(Role.ROLE_ADMIN)
//					.phone("01000001111")
//					.build());
//		}
//		catch (Exception e) {
//		}
//	}
//	
//
//}
