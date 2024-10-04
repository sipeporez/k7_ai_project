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
public class WaveDataArrayFlaskDTO {
	private String asset_id;
	private Integer created_at;
	private Float[] x1; private Float[] y1; private Float[] z1;
	private Float[] x2; private Float[] y2; private Float[] z2;
	private Float[] x3; private Float[] y3; private Float[] z3;
	private Float[] x4; private Float[] y4; private Float[] z4;
	private Float[] x5; private Float[] y5; private Float[] z5;
	private Float[] x6; private Float[] y6; private Float[] z6;
	private Float[] x7; private Float[] y7; private Float[] z7;
	private Float[] x8; private Float[] y8; private Float[] z8;
	private Float[] x9; private Float[] y9; private Float[] z9;
	private Float[] x10; private Float[] y10; private Float[] z10;
	private Float[] x11; private Float[] y11; private Float[] z11;
	private Float[] x12; private Float[] y12; private Float[] z12;
}