package com.motorPM.domain.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class BookmarkDTO {
	
	private String asset_id;
	private Integer start_at;
	private Integer end_at;
	private String bookmark_name;
	private List<String> cols;
	private List<String> regidate;
}
