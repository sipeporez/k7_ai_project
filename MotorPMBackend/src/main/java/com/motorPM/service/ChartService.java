package com.motorPM.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motorPM.domain.DTO.ChartDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChartService {

	private final EntityManager em;

	// 기간과 asset_id, 조회할 컬럼을 받아서 SigData를 조회하는 메서드
	public List<Map<String, Object>> getSigdataChart(ChartDTO data) {

		ObjectMapper mapper = new ObjectMapper();
		// ChartDTO를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(data, JsonNode.class);

		String asset_id = jsonData.path("asset_id").asText();
		int startTime = jsonData.path("start_at").asInt();
		int endTime = jsonData.path("end_at").asInt();

		// cols 배열을 추출
		JsonNode colsNode = jsonData.path("cols");

		// cols 배열의 각 요소를 String으로 변환
		List<String> colsList = StreamSupport.stream(colsNode.spliterator(), false).map(col -> col.asText())
				.collect(Collectors.toList());
		// sql 쿼리문 작성
		String query = "SELECT sg.asset_id, mst.asset_name, created_at, ";
		query += String.join(", ", colsList);
		query += " FROM ics_asset_sigdata sg, ics_asset_mst mst ";
		query += " WHERE sg.asset_id = mst.asset_id ";
		query += " AND mst.asset_id = '" + asset_id + "'";
		query += " AND created_at BETWEEN " + startTime + " AND " + endTime;

		System.out.println(query);
		Query sql = em.createNativeQuery(query);

		@SuppressWarnings("unchecked")
		List<Object[]> results = sql.getResultList();
		List<Map<String, Object>> list = new ArrayList<>();

		// 쿼리 결과를 저장
		for (Object[] result : results) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("asset_id", result[0]);
			map.put("asset_name", result[1]);
			map.put("created_at", result[2]);
			// cols에 따라 동적으로 값 추가
			for (int i = 0; i < colsList.size(); i++) {
				map.put(colsList.get(i), result[i + 3]); // 0, 1, 2는 이미 사용했으므로 +3
			}
			list.add(map);
		}
		return list;
	}
	
	// created_at과 asset_id를 받아서 detailData를 모두 조회하는 메서드
		public List<Map<String, Object>> getDetailDataAll(ChartDTO data) {
			
			ObjectMapper mapper = new ObjectMapper();
			// ChartDTO를 JsonNode로 변환
			JsonNode jsonData = mapper.convertValue(data, JsonNode.class);
			
			String asset_id = jsonData.path("asset_id").asText();
			int created_at = jsonData.path("created_at").asInt();
			String query = null;
			
			query = "SELECT asset_id, created_at, waveform_x, waveform_y, waveform_z, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp " 
					+ "FROM pm_data.ics_asset_wavedata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at <= :created_at "
					+ "ORDER BY created_at desc "
					+ "LIMIT 1;";

			Query sql = em.createNativeQuery(query);
			sql.setParameter("asset_id", asset_id);
			sql.setParameter("created_at", created_at);
			
			@SuppressWarnings("unchecked")
			List<Object[]> results = sql.getResultList();
			List<Map<String, Object>> list = new ArrayList<>();
			
			// 쿼리 결과를 저장
			for (Object[] result : results) {
				Map<String, Object> map = new LinkedHashMap<>();
				map.put("asset_id", result[0]);
				map.put("created_at", result[1]);
				
				// String 타입의 데이터를 쉼표로 구분하고 자른 뒤 Double 타입으로 반환
				List<Double> waveform_x = Arrays.stream
						(result[2].toString().split(","))
						.map(String::trim)
						.map(Double::parseDouble)
						.collect(Collectors.toList());
				map.put("waveform_x", waveform_x);
				
				List<Double> waveform_y = Arrays.stream
						(result[3].toString().split(","))
						.map(String::trim)
						.map(Double::parseDouble)
						.collect(Collectors.toList());
				map.put("waveform_y", waveform_y);
				
				List<Double> waveform_z = Arrays.stream
						(result[4].toString().split(","))
						.map(String::trim)
						.map(Double::parseDouble)
						.collect(Collectors.toList());
				map.put("waveform_z", waveform_z);
				
				List<Double> spectrum_x = Arrays.stream
						(result[5].toString().split(","))
						.map(String::trim)
						.map(Double::parseDouble)
						.collect(Collectors.toList());
				map.put("spectrum_x", spectrum_x);
				
				List<Double> spectrum_y = Arrays.stream
						(result[6].toString().split(","))
						.map(String::trim)
						.map(Double::parseDouble)
						.collect(Collectors.toList());
				map.put("spectrum_y", spectrum_y);
				
				List<Double> spectrum_z = Arrays.stream
						(result[7].toString().split(","))
						.map(String::trim)
						.map(Double::parseDouble)
						.collect(Collectors.toList());
				map.put("spectrum_z", spectrum_z);

				list.add(map);
			}
			return list;
		}
	
	// created_at과 asset_id, 구분자(WAVEFORM,SPECTRUM)를 받아서 detailData(wave_data)를 조회하는 메서드
	public List<Map<String, Object>> getDetailData(ChartDTO data) {
		
		ObjectMapper mapper = new ObjectMapper();
		// ChartDTO를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(data, JsonNode.class);
		
		String asset_id = jsonData.path("asset_id").asText();
		int created_at = jsonData.path("created_at").asInt();
		String gubun = jsonData.path("type").asText();
		String query = null;
		
		// 구분자에 따라 웨이브폼과 스펙트럼 쿼리문 분리
		if (gubun.trim().equals("WAVEFORM")) {
			query = "SELECT asset_id, created_at, waveform_x, waveform_y, waveform_z " 
					+ "FROM pm_data.ics_asset_wavedata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at <= :created_at "
					+ "ORDER BY created_at desc "
					+ "LIMIT 1;";
		}
		else {
			query = "SELECT asset_id, created_at, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp " 
					+ "FROM pm_data.ics_asset_wavedata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at <= :created_at "
					+ "ORDER BY created_at desc "
					+ "LIMIT 1;"; 
		}

		Query sql = em.createNativeQuery(query);
		sql.setParameter("asset_id", asset_id);
		sql.setParameter("created_at", created_at);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = sql.getResultList();
		List<Map<String, Object>> list = new ArrayList<>();
		
		// 쿼리 결과를 저장
		for (Object[] result : results) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("asset_id", result[0]);
			map.put("created_at", result[1]);
			
			// String 타입의 데이터를 쉼표로 구분하고 자른 뒤 Double 타입으로 반환
			List<Double> x = Arrays.stream
					(result[2].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("x", x);
			
			List<Double> y = Arrays.stream
					(result[3].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("y", y);
			
			List<Double> z = Arrays.stream
					(result[4].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("z", z);

			list.add(map);
		}
		return list;
	}
	
	// created_at과 asset_id를 받아서 Flask에 Spectrum을 전달하는 메서드
	public List<Map<String, Object>> getSpectrumToFlask(ChartDTO data) {
		
		ObjectMapper mapper = new ObjectMapper();
		// ChartDTO를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(data, JsonNode.class);
		
		String asset_id = jsonData.path("asset_id").asText();
		int created_at = jsonData.path("created_at").asInt();
		String query = null;
		
		query = "SELECT asset_id, created_at, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp " 
				+ "FROM pm_data.ics_asset_wavedata "
				+ "WHERE asset_id = :asset_id "
				+ "AND created_at <= :created_at "
				+ "ORDER BY created_at desc "
				+ "LIMIT 1;"; 
		
		Query sql = em.createNativeQuery(query);
		sql.setParameter("asset_id", asset_id);
		sql.setParameter("created_at", created_at);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = sql.getResultList();
		List<Map<String, Object>> list = new ArrayList<>();
		
		// 쿼리 결과를 저장
		for (Object[] result : results) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("asset_id", result[0]);
			map.put("created_at", result[1]);
			
			// String 타입의 데이터를 쉼표로 구분하고 자른 뒤 Double 타입으로 반환
			List<Double> x = Arrays.stream
					(result[2].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("x", x);
			
			List<Double> y = Arrays.stream
					(result[3].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("y", y);
			
			List<Double> z = Arrays.stream
					(result[4].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("z", z);
			
			list.add(map);
		}
		return list;
	}

}
