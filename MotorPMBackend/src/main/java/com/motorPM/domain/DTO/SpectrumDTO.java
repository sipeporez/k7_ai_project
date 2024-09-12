package com.motorPM.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// 실시간 차트용 DTO
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpectrumDTO {
	private String asset_id;
	private Integer created_at;
	private Float spectrum_x;
	private Float spectrum_y;
	private Float spectrum_z;
}