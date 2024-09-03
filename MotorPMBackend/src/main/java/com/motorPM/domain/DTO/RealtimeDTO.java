package com.motorPM.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//BE가 연결된 FE에게 전달하는 데이터 클래스 정의
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeDTO {
	private String asset_id;
	private Integer created_at;
	private Float spectrum_x;
	private Float spectrum_y;
	private Float spectrum_z;
}