package com.motorPM.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.motorPM.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	@Query("SELECT m FROM Member m WHERE m.userid = :userid")
    Optional<Member> findByUserid(@Param("userid") String userid);
	
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
	
	// 임시 temperature, voltage 데이터 -> 메인페이지 차트 옆에 사용
	@Query(nativeQuery = true,
			value = "SELECT asset_id, created_at, temperature, voltage "
					+ "FROM ics_asset_sigdata "
					+ "WHERE asset_id = :asset_id "
					+ "AND created_at = (SELECT max(created_at) FROM ics_asset_sigdata WHERE asset_id = :asset_id)"
			)
	Object realDataTempVoltResult(
			@Param("asset_id") String asset_id);
	
}
