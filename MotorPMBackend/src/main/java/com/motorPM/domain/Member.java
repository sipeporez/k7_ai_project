package com.motorPM.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idx;
	
	@Column(nullable = false, length = 16, unique = true)
	private String userid;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, length = 10)
	private String username;
	
	@Column(length = 11)
	private String phone;
	
	@Builder.Default
	@Column(nullable = false)
	private Role role = Role.ROLE_MEMBER;
	
	@Builder.Default
	@Column(nullable = false)
	private LocalDateTime regi_date = LocalDateTime.now();
	
	@Column
	private LocalDateTime login_date;
}
