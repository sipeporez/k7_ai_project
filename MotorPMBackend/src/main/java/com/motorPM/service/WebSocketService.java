package com.motorPM.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.motorPM.domain.DTO.SpectrumDTO;
import com.motorPM.domain.DTO.WaveformDTO;
import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketService {
	private final MemberRepository mr;
		
	public List<SpectrumDTO> getSpectrum(String message) {
		List<SpectrumDTO> list = new ArrayList<>();
		List<Object[]> result = mr.realDataSpectrumResult(message);
		for (Object[] row : result) {
			list.add(SpectrumDTO.builder()
					.asset_id(row[0].toString())
					.created_at((Integer) row[1])
					.spectrum_x((Float) row[2])
					.spectrum_y((Float) row[3])
					.spectrum_z((Float) row[4])
					.build());
		}
		return list;
	}
	public List<WaveformDTO> getWaveform(String message) {
		List<WaveformDTO> list = new ArrayList<>();
		List<Object[]> result = mr.realDataWaveResult(message);
		for (Object[] row : result) {
			list.add(WaveformDTO.builder()
					.asset_id(row[0].toString())
					.created_at((Integer) row[1])
					.waveform_x((Float) row[2])
					.waveform_y((Float) row[3])
					.waveform_z((Float) row[4])
					.build());
		}
		return list;
	}
}
