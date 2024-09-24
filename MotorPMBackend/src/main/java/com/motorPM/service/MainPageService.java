package com.motorPM.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.motorPM.domain.DTO.TempVoltDTO;
import com.motorPM.domain.DTO.WaveformArrayDTO;
import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainPageService {
	
	private final MemberRepository mr;
	
	// 온도, 전압 메서드
	public TempVoltDTO getTempVolt(String asset_id) {
		Object[] result = (Object[]) mr.realDataTempVoltResult(asset_id);
		
		return TempVoltDTO.builder()
				.asset_id(result[0].toString())
				.created_at((Integer) result[1])
				.temperature((Float) result[2])
				.voltage((Float) result[3])
				.build();
	}
	
	// 파형 차트 메서드
	public WaveformArrayDTO getWaveform(String asset_id) {
		Object[] result = (Object[]) mr.WaveformResult(asset_id);
		List<Float[]> wave = new ArrayList<>(3);
		
		for (int i = 2; i < 5; i++) {
			String[] stringArr = result[i].toString().split(",");
			Float[] floatArr = new Float[stringArr.length];
			
			for (int j = 0; j < stringArr.length; j++) {
				floatArr[j] = Float.parseFloat(stringArr[j].trim());
			}
			wave.add(floatArr);
        }

		return WaveformArrayDTO.builder()
				.asset_id(result[0].toString())
				.created_at((Integer) result[1])
				.waveform_x(wave.get(0))
				.waveform_y(wave.get(1))
				.waveform_z(wave.get(2))
				.build();
	}

}
