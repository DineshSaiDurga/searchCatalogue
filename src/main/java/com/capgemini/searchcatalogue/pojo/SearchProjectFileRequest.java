package com.capgemini.searchcatalogue.pojo;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class SearchProjectFileRequest {

	@Nullable
	private String path;
	
	private String keyword;
	
	private String url;
	
	private String token;
	
	private Integer projectId;
	
}
