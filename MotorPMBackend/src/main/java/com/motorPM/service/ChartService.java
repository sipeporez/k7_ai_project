package com.motorPM.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motorPM.domain.DTO.BookmarkDTO;
import com.motorPM.domain.DTO.ChartDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChartService {

	public String getUserIDFromToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return authentication.getName();
		}
		return null;
	}

	private final EntityManager em;

	// 기간과 asset_name, 조회할 컬럼을 받아서 SigData를 조회하는 메서드
	public List<Map<String, Object>> getSigdataChart(ChartDTO data) {

		ObjectMapper mapper = new ObjectMapper();
		// Object를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(data, JsonNode.class);

		String asset_name = jsonData.path("asset_name").asText();
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
		query += " AND mst.asset_name = '" + asset_name + "'";
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
	
	// 기간과 asset_name, 조회할 컬럼을 받아서 detailData를 조회하는 메서드
	public List<Map<String, Object>> getDetailData(ChartDTO data) {
		
		ObjectMapper mapper = new ObjectMapper();
		// Object를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(data, JsonNode.class);
		
		String asset_id = jsonData.path("asset_id").asText();
		int created_at = jsonData.path("created_at").asInt();
		
		// sql 쿼리문 작성
		String query = "SELECT asset_id, created_at, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp " 
				+ "FROM pm_data.ics_asset_wavedata "
				+ "WHERE asset_id = :asset_id AND created_at = :created_at";
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
			List<Double> spectrum_x_amp = Arrays.stream
					(result[2].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("spectrum_x_amp", spectrum_x_amp);
			
			List<Double> spectrum_y_amp = Arrays.stream
					(result[3].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("spectrum_y_amp", spectrum_y_amp);
			
			List<Double> spectrum_z_amp = Arrays.stream
					(result[4].toString().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.collect(Collectors.toList());
			map.put("spectrum_z_amp", spectrum_z_amp);

			list.add(map);
		}
		return list;
	}

	// 기간과 asset_name, 조회할 컬럼을 받아서 bookmark에 저장하는 메서드
	// userid 인증 안될 경우 401, 성공할 경우 200
	@Transactional
	public int saveBookmark(BookmarkDTO data) {
		String userid = getUserIDFromToken();
		if (userid == null) return HttpStatus.UNAUTHORIZED.value();

		ObjectMapper mapper = new ObjectMapper();
		// Object를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(data, JsonNode.class);

		String asset_name = jsonData.path("asset_name").asText();
		int startTime = jsonData.path("start_at").asInt();
		int endTime = jsonData.path("end_at").asInt();
		String nickname = jsonData.path("bookmark_name").asText();

		// cols 배열을 추출
		JsonNode colsNode = jsonData.path("cols");

		// cols 배열의 각 요소를 String으로 변환
		List<String> colsList = StreamSupport.stream(colsNode.spliterator(), false)
				.map(col -> col.asText())
				.collect(Collectors.toList());
		
		String cols = String.join(", ", colsList);
		
		String bookmark = "INSERT INTO bookmark (`userid`, `nickname`,`cols`,`asset_name`, `startTime`, `endTime`)"
				+ " VALUES (:userid, :nickname, :cols, :asset_name, :startTime, :endTime);";
		Query sql = em.createNativeQuery(bookmark);
		sql.setParameter("userid", userid);
		sql.setParameter("nickname", nickname);
		sql.setParameter("cols", cols);
		sql.setParameter("asset_name", asset_name);
		sql.setParameter("startTime", startTime);
		sql.setParameter("endTime", endTime);
		sql.executeUpdate();

		return HttpStatus.OK.value();
	}
	
	// 토큰 기반으로 userid에 해당하는 북마크를 불러오는 메서드
	public List<Map<String, Object>> loadBookmark() {
		String userid = getUserIDFromToken();
		if (userid == null) return null;
		
		String query = "SELECT nickname, cols, asset_name, startTime, endTime, regidate"
				+ " FROM bookmark WHERE userid = :userid";
		Query sql = em.createNativeQuery(query);
		sql.setParameter("userid", userid);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = sql.getResultList();
		List<Map<String, Object>> list = new ArrayList<>();

		// 쿼리 결과를 저장
		for (Object[] result : results) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("nickname", result[0]);
			
			// cols 컬럼을 쉼표로 분리하여 리스트로 변환
	        String colsStr = (String) result[1];
	        List<String> colsList = Arrays.asList(colsStr.split("\\s*,\\s*")); // 쉼표로 분리
	        map.put("cols", colsList);
	        
			map.put("asset_name", result[2]);
			map.put("startTime", result[3]);
			map.put("endTime", result[4]);
			map.put("regidate", result[5]);
			
			list.add(map);
		}
		return list;
	}

	// 토큰 기반으로 userid의 regidate에 해당하는 북마크를 삭제하는 메서드
	// userid 인증 안될 경우 401, 성공할 경우 200
	@Transactional
	public int deleteBookmark(Object delbook) {
		String userid = getUserIDFromToken();
		if (userid == null) return HttpStatus.UNAUTHORIZED.value();
		
		ObjectMapper mapper = new ObjectMapper();
		// Object를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(delbook, JsonNode.class);
		
		// regidate 배열을 추출
		JsonNode dateNode = jsonData.path("regidate");

		// regidate 배열의 각 요소를 String으로 변환
		List<String> dateList = StreamSupport.stream(dateNode.spliterator(), false)
				.map(col -> col.asText())
				.collect(Collectors.toList());
		String regidate = "'" + String.join("', '", dateList)+"'";
		String query = "DELETE FROM bookmark WHERE regidate in (" + regidate + ") AND `userid` = :userid";
		Query sql = em.createNativeQuery(query);
		sql.setParameter("userid", userid);
		sql.executeUpdate();
		
		return HttpStatus.OK.value();
	}
}
