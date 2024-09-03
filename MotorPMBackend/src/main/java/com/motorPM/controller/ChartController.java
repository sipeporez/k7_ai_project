package com.motorPM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.motorPM.service.ChartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChartController {

	private final ChartService cs;

	@PostMapping("/charts") // GET에서 POST로 변경(컬럼을 배열형식으로 받기 때문)
	public ResponseEntity<?> getSigdata(@RequestBody Object sig) {
		return ResponseEntity.ok(cs.getSigdataChart(sig));
		
	}
	
	@PostMapping("/charts/savebookmark")
	public ResponseEntity<?> saveBookmark(@RequestBody Object sig) {
		return ResponseEntity.status(cs.saveBookmark(sig)).body(null);
		
	}
	
	@GetMapping("/charts/loadbookmark")
	public ResponseEntity<?> loadBookmark() {
		return ResponseEntity.ok(cs.loadBookmark());
	}
	
	@PostMapping("/charts/delbookmark")
	public ResponseEntity<?> deleteBookmark(@RequestBody Object sig) {
		return ResponseEntity.status(cs.deleteBookmark(sig)).body(null);
	}

}
