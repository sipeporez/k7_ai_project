package com.motorPM.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.motorPM.domain.DTO.WaveDataDTO;
import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketService {
	private final MemberRepository mr;
		
	public List<WaveDataDTO> getWaveData(String message, String gubun) {
		List<WaveDataDTO> list = new ArrayList<>();
		List<Object[]> result = new ArrayList<>();
		if (gubun.trim().equals("WAVEFORM")) result = mr.realDataWaveResult(message);
		else if (gubun.trim().equals("SPECTRUM")) result = mr.realDataSpectrumResult(message);
		
		for (Object[] row : result) {
			list.add(WaveDataDTO.builder()
					.asset_id(row[0].toString())
					.created_at((Integer) row[1])
					.x((Float) row[2])
					.y((Float) row[3])
					.z((Float) row[4])
					.build());
		}
		return list;
	}
}
