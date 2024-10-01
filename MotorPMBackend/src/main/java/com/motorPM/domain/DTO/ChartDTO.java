package com.motorPM.domain.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ChartDTO {
	
	private String asset_id;
	private String asset_name;
	private Integer start_at;
	private Integer end_at;
	private Integer created_at;
	private List<String> cols;
	private String type;
	
}
