package com.motorPM.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motorPM.domain.DTO.BookmarkDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChartBookmarkService {	// Jackson 이용

	private final GetToken gt;
	private final EntityManager em;
	private final FlaskService fs; // unixtime 변환용

	// 기간과 asset_id, 조회할 컬럼을 받아서 bookmark에 저장하는 메서드
	// userid 인증 안될 경우 401, 성공할 경우 200
	@Transactional
	public int saveBookmark(BookmarkDTO data) {
		String userid = gt.getUserIDFromToken();
		if (userid == null)
			return HttpStatus.UNAUTHORIZED.value();

		ObjectMapper mapper = new ObjectMapper();
		// ChartDTO를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(data, JsonNode.class);

		String asset_id = jsonData.path("asset_id").asText();
		int startTime = jsonData.path("start_at").asInt();
		int endTime = jsonData.path("end_at").asInt();
		String nickname = jsonData.path("bookmark_name").asText();

		// cols 배열을 추출
		JsonNode colsNode = jsonData.path("cols");

		// cols 배열의 각 요소를 String으로 변환
		List<String> colsList = StreamSupport.stream(colsNode.spliterator(), false).map(col -> col.asText())
				.collect(Collectors.toList());

		String cols = String.join(", ", colsList);

		String bookmark = "INSERT INTO bookmark (`userid`, `nickname`,`cols`,`asset_id`, `startTime`, `endTime`)"
				+ " VALUES (:userid, :nickname, :cols, :asset_id, :startTime, :endTime);";
		Query sql = em.createNativeQuery(bookmark);
		sql.setParameter("userid", userid);
		sql.setParameter("nickname", nickname);
		sql.setParameter("cols", cols);
		sql.setParameter("asset_id", asset_id);
		sql.setParameter("startTime", startTime);
		sql.setParameter("endTime", endTime);
		sql.executeUpdate();

		return HttpStatus.OK.value();
	}

	// 토큰 기반으로 userid에 해당하는 북마크를 불러오는 메서드
	public List<Map<String, Object>> loadBookmark() {
		String userid = gt.getUserIDFromToken();
		if (userid == null)
			return null;

		String query = "SELECT nickname, cols, asset_id, startTime, endTime, DATE_FORMAT(regidate, '%Y-%m-%d %H:%i:%s') as regidate "
				+ "FROM bookmark WHERE userid = :userid";
		Query sql = em.createNativeQuery(query);
		sql.setParameter("userid", userid);

		@SuppressWarnings("unchecked")
		List<Object[]> results = sql.getResultList();
		List<Map<String, Object>> list = new ArrayList<>();

		// 쿼리 결과를 저장
		for (Object[] result : results) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("bookmark_name", result[0]);

			// cols 컬럼을 쉼표로 분리하여 리스트로 변환
			String colsStr = (String) result[1];
			List<String> colsList = Arrays.asList(colsStr.split("\\s*,\\s*")); // 쉼표로 분리
			map.put("cols", colsList);
			map.put("asset_id", result[2]);
			map.put("startTime", result[3]);
			map.put("endTime", result[4]);
			map.put("regidate", result[5]);
			map.put("startDate", fs.unixToKoreanTime((Integer)result[3]));
			map.put("endDate", fs.unixToKoreanTime((Integer)result[4]));

			list.add(map);
		}
		return list;
	}

	// 토큰 기반으로 userid의 regidate에 해당하는 북마크를 삭제하는 메서드
	// userid 인증 안될 경우 401, 성공할 경우 200
	@Transactional
	public int deleteBookmark(BookmarkDTO delbook) {
		String userid = gt.getUserIDFromToken();
		if (userid == null)
			return HttpStatus.UNAUTHORIZED.value();

		ObjectMapper mapper = new ObjectMapper();
		// ChartDTO를 JsonNode로 변환
		JsonNode jsonData = mapper.convertValue(delbook, JsonNode.class);

		// regidate 배열을 추출
		JsonNode dateNode = jsonData.path("regidate");

		// regidate 배열의 각 요소를 String으로 변환
		List<String> dateList = StreamSupport.stream(dateNode.spliterator(), false).map(col -> col.asText())
				.collect(Collectors.toList());
		String regidate = "'" + String.join("', '", dateList) + "'";
		String query = "DELETE FROM bookmark WHERE regidate in (" + regidate + ") AND `userid` = :userid";
		Query sql = em.createNativeQuery(query);
		sql.setParameter("userid", userid);
		sql.executeUpdate();

		return HttpStatus.OK.value();
	}
}
