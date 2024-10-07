package com.motorPM.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.motorPM.domain.DTO.AnomalyDTO;
import com.motorPM.domain.DTO.WaveDataArrayFlaskDTO;
import com.motorPM.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 실제 서비스 시 가장 최근까지 등록된 하루치(12개) spectrum 데이터 x,y,z를 병합하여 전송
// 에셋 아이디는 현재 5528~ 로 고정됨 -> controller에서 수정할 것
public class FlaskService {
	
	private final MemberRepository mr;
	
	public String unixToKoreanTime(Integer unixTime) {
        // 유닉스 타임을 밀리초로 변환
        long millis = (long) unixTime * 1000;

        // Date 객체 생성
        Date date = new Date(millis);

        // 한국 시간으로 변환
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        calendar.setTime(date);

        // 형식화된 문자열 생성 (예: "yyyy-MM-dd HH:mm:ss")
        String formattedDate = String.format("%tF %<tT", calendar);
        return formattedDate;
    }
	
	
	public void anomalyDetected(String asset_id, Integer created_at, Float model_value) {
		String[] model_result = {"CRITICAL","INSPECT"};
		if (model_value <= 0.5 && model_value >= 0.25) 
			mr.anomalyDectected(asset_id, created_at, model_value, model_result[1]);
		else if (model_value < 0.25)
			mr.anomalyDectected(asset_id, created_at, model_value, model_result[0]);
	}
	
	public List<AnomalyDTO> getAnomalyTable() {
		List<Object[]> results = mr.getAnomalyTable();
		List<AnomalyDTO> list = new ArrayList<>();
		for (Object[] res : results) {
			list.add(AnomalyDTO.builder()
					.asset_id(res[0].toString())
					.asset_name(res[1].toString())
					.created_at(unixToKoreanTime((Integer) res[2]))
					.detected_date((Timestamp) res[3])
					.model_value((Float) res[4])
					.model_result(res[5].toString())
					.build());
		}
		return list;
	}
	
	// 플라스크용 스펙트럼 데이터 메서드 (12개 - 하루치)
	public WaveDataArrayFlaskDTO getSpectrumDaily(String asset_id) {
		List<Object[]> result = null;
		result = mr.SpectrumResultFlask(asset_id);
		
		List<Float[]> wave = new ArrayList<>(36);
		for (int i = 0; i < result.size(); i++) {
			for (int j = 2; j < 5; j++) {
				String[] stringArr = result.get(i)[j].toString().split(",");
				Float[] floatArr = new Float[stringArr.length];
				
				for (int k = 0; k < stringArr.length; k++) {
					floatArr[k] = Float.parseFloat(stringArr[k].trim());
				}
				wave.add(floatArr);
			}
		}
		return WaveDataArrayFlaskDTO.builder()
				.x1(wave.get(0)).y1(wave.get(1)).z1(wave.get(2))
				.x2(wave.get(3)).y2(wave.get(4)).z2(wave.get(5))
				.x3(wave.get(6)).y3(wave.get(7)).z3(wave.get(8))
				.x4(wave.get(9)).y4(wave.get(10)).z4(wave.get(11))
				.x5(wave.get(12)).y5(wave.get(13)).z5(wave.get(14))
				.x6(wave.get(15)).y6(wave.get(16)).z6(wave.get(17))
				.x7(wave.get(18)).y7(wave.get(19)).z7(wave.get(20))
				.x8(wave.get(21)).y8(wave.get(22)).z8(wave.get(23))
				.x9(wave.get(24)).y9(wave.get(25)).z9(wave.get(26))
				.x10(wave.get(27)).y10(wave.get(28)).z10(wave.get(29))
				.x11(wave.get(30)).y11(wave.get(31)).z11(wave.get(32))
				.x12(wave.get(33)).y12(wave.get(34)).z12(wave.get(35))
				.build();
	}

}
