package com.motorPM.domain.DTO;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnomalyDTO {
	
	private String asset_id;
	private String asset_name;
	private String created_at;
	private Timestamp detected_date;
	private Float model_value;
	private String model_result;
}
