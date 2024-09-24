package com.motorPM.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// 메인페이지 옆 temperature, voltage용 DTO
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempVoltDTO {
	
	private String asset_id;
	private Integer created_at;
	private Float temperature;
	private Float voltage;
}
