package com.motorPM.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 실제 서비스 시 가장 최근까지 등록된 하루치(12개) spectrum 데이터 x,y,z를 병합하여 전송
// 에셋 아이디는 현재 5528~ 로 고정됨 -> controller에서 수정할 것
public class FlaskService {
	
	private final MemberRepository mr;
	
	public Double[] getLastestSpectrum(String asset_id) {
	    List<Object[]> result = mr.lastestSpectrumData(asset_id);
	    Double[] arr = new Double[73728];
	    int index = 0;

	    for (Object[] row : result) {
	        for (int i = 2; i < 5; i++) {
	            String[] stringArr = ((String) row[i]).split(",");
	            for (int j = 1; j < stringArr.length; j++) {
	                arr[index++] = Double.parseDouble(stringArr[j].trim());
	            }
	        }
	    }
	    return arr;
	}
}
