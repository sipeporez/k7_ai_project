package com.motorPM.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.motorPM.domain.Member;
import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
	private final MemberRepository mr;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Member member = mr.findByUserid(username).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
		return new User(
				member.getUserid(),
				member.getPassword(),
				AuthorityUtils.createAuthorityList(member.getRole().toString()));
	}
}
