package com.capgemini.searchcatalogue.pojo;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class SearchFileRequest {

	@Nullable
	private String path;

	private String keyword;
	
}
