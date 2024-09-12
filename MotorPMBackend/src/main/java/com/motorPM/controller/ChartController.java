package com.motorPM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.motorPM.domain.DTO.BookmarkDTO;
import com.motorPM.domain.DTO.ChartDTO;
import com.motorPM.persistence.MemberRepository;
import com.motorPM.service.ChartService;
import com.motorPM.service.WebSocketService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChartController {

	private final ChartService cs;
	private final WebSocketService ws;
	
	@GetMapping("/test") // GET에서 POST로 변경(컬럼을 배열형식으로 받기 때문)
	public ResponseEntity<?> getSigdata() {
		return ResponseEntity.ok(ws.getSpectrum("MILL"));
	}

	@PostMapping("/charts") // GET에서 POST로 변경(컬럼을 배열형식으로 받기 때문)
	public ResponseEntity<?> getSigdata(@RequestBody ChartDTO data) {
		return ResponseEntity.ok(cs.getSigdataChart(data));
		
	}
	
//	@PostMapping("/charts/detail")
//	public ResponseEntity<?> getDetailData(@RequestBody ChartDTO data) {
//		return ResponseEntity.ok(cs.getDetailData(data));
//	}
	
	@PostMapping("/charts/savebookmark")
	public ResponseEntity<?> saveBookmark(@RequestBody BookmarkDTO data) {
		return ResponseEntity.status(cs.saveBookmark(data)).body(null);
		
	}
	
	@GetMapping("/charts/loadbookmark")
	public ResponseEntity<?> loadBookmark() {
		return ResponseEntity.ok(cs.loadBookmark());
	}
	
	@PostMapping("/charts/delbookmark")
	public ResponseEntity<?> deleteBookmark(@RequestBody BookmarkDTO data) {
		return ResponseEntity.status(cs.deleteBookmark(data)).body(null);
	}

}
