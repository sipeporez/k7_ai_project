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
public class WaveDataArrayDTO {
	private String asset_id;
	private Integer created_at;
	private Float[] x;
	private Float[] y;
	private Float[] z;
	private String model;
}