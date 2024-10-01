package com.motorPM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.motorPM.service.FlaskService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FlaskController {
	
	private final FlaskService fs;
	
	@GetMapping("/flask")
	public ResponseEntity<?> getLastestSpectrum(@RequestParam String asset_id) {
		return ResponseEntity.ok(fs.getLastestSpectrum(asset_id));
	}
	

}
