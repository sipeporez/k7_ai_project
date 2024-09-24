package com.motorPM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.motorPM.domain.DTO.TempVoltDTO;
import com.motorPM.service.MainPageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainPageController {

	private final MainPageService ms;

	@PostMapping("/tempvolt") // 메인페이지(실시간) 온도, 전압
	public ResponseEntity<?> getTempVoltData(@RequestBody TempVoltDTO dto) {
		return ResponseEntity.ok(ms.getTempVolt(dto.getAsset_id()));
		
	}
	
	@PostMapping("/waveform") // 메인페이지 파형 그래프 데이터
	public ResponseEntity<?> getWaveformData(@RequestBody TempVoltDTO dto) {
		return ResponseEntity.ok(ms.getWaveform(dto.getAsset_id()));
	}
	
}
