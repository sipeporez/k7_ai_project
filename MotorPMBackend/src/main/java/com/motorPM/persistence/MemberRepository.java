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
	
	
	@Query(nativeQuery = true, 
			value = "SELECT asset_id, created_at, spectrum_x_amp, spectrum_y_amp, spectrum_z_amp "
					+ "FROM realtime_spectrumdata "
					+ "WHERE asset_id = (SELECT asset_id FROM ics_asset_mst WHERE asset_name = :asset_name LIMIT 1)"
					)
	List<Object[]> realDataSpectrumResult(
			@Param("asset_name") String asset_name);
	
	@Query(nativeQuery = true, 
			value = "SELECT asset_id, created_at, waveform_x, waveform_y, waveform_z "
					+ "FROM realtime_wavedata "
					+ "WHERE asset_id = (SELECT asset_id FROM ics_asset_mst WHERE asset_name = :asset_name LIMIT 1)"
			)
	List<Object[]> realDataWaveResult(
			@Param("asset_name") String asset_name);
}
