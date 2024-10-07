package com.motorPM.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.motorPM.domain.Member;

import jakarta.transaction.Transactional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	@Query("SELECT m FROM Member m WHERE m.userid = :userid")
    Optional<Member> findByUserid(@Param("userid") String userid);
	
	@Modifying
    @Transactional
	@Query(nativeQuery = true,
			value = "INSERT INTO anomaly_detected (asset_id, asset_name, created_at, model_value, model_result)"
					+ " VALUES (:asset_id, (SELECT asset_name FROM ics_asset_mst WHERE asset_id = :asset_id), :created_at, :model_value, :model_result) "
					+ "ON DUPLICATE KEY UPDATE "
					+ "model_value = VALUES(model_value), "
					+ "model_result = VALUES(model_result);" )
	void anomalyDectected(
			@Param("asset_id") String asset_id,
			@Param("created_at") Integer created_at,
			@Param("model_value") Float model_value,
			@Param("model_result") String model_result
			);
	
	@Query(nativeQuery = true,
			value = "SELECT asset_id, asset_name, created_at, detected_date, model_value, model_result "
					+ "FROM anomaly_detected")
	List<Object[]> getAnomalyTable();
	
	// 가장 최신의 12개 스펙트럼 데이터 -> 웹서비스에서 사용
	@Query(nativeQuery = true,
			value = "SELECT * FROM ( "
				    + "SELECT asset_id, created_at, " 
			        + "spectrum_x_amp, spectrum_y_amp, spectrum_z_amp "
			        + "FROM ics_asset_wavedata "
			        + "WHERE asset_id = :asset_id "
			        + "ORDER BY created_at DESC "
			        + "LIMIT 12) "
			        + "as subquery "
			        + "ORDER BY created_at ASC")
	List<Object[]> lastestSpectrumData(@Param("asset_id") String asset_id);
	
	
	// 임시 스펙트럼 데이터 -> 메인페이지 실시간 차트
	@Query(nativeQuery = true, 
			value = "SELECT asset_id, created_at, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp "
					+ "FROM realtime_spectrumdata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at = (SELECT max(created_at) FROM realtime_spectrumdata WHERE asset_id = :asset_id) "
					+ "ORDER BY idx "
					)
	List<Object[]> realDataSpectrumResult(
			@Param("asset_id") String message);

	// 임시 웨이브 데이터 -> 메인페이지 실시간 차트
	@Query(nativeQuery = true, 
			value = "SELECT asset_id, created_at, waveform_x, waveform_y, waveform_z "
					+ "FROM realtime_wavedata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at = (SELECT max(created_at) FROM realtime_wavedata WHERE asset_id = :asset_id) "
					+ "ORDER BY idx "
					)
	List<Object[]> realDataWaveResult(
			@Param("asset_id") String message);
	
	// 임시 웨이브 데이터 -> 메인페이지 파형 차트
	@Query(nativeQuery = true, 
			value = "SELECT asset_id, created_at, waveform_x, waveform_y, waveform_z "
					+ "FROM ics_asset_wavedata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at = (SELECT max(created_at) FROM ics_asset_wavedata WHERE asset_id = :asset_id) "
					)
	Object WaveformResult(
			@Param("asset_id") String message);
	
	// 임시 웨이브 데이터 -> 메인페이지 스펙트럼 차트
	@Query(nativeQuery = true, 
			value = "SELECT asset_id, created_at, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp "
					+ "FROM ics_asset_wavedata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at = (SELECT max(created_at) FROM ics_asset_wavedata WHERE asset_id = :asset_id) "
			)
	Object SpectrumResult(
			@Param("asset_id") String message);
	
	// 임시 temperature, voltage 데이터 -> 메인페이지 차트 옆에 사용
	@Query(nativeQuery = true,
			value = "SELECT asset_id, created_at, temperature, voltage "
					+ "FROM ics_asset_sigdata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at = (SELECT max(created_at) FROM ics_asset_sigdata WHERE asset_id = :asset_id)"
			)
	Object realDataTempVoltResult(
			@Param("asset_id") String asset_id);
	
	// Flask 데이터
	@Query(nativeQuery = true, 
			value = "SELECT asset_id, created_at, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp "
					+ "FROM ics_asset_wavedata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at <= (SELECT max(created_at) FROM ics_asset_wavedata WHERE asset_id = :asset_id) "
					+ "LIMIT 12"
			)
	List<Object[]> SpectrumResultFlask(
			@Param("asset_id") String message);
	
}
