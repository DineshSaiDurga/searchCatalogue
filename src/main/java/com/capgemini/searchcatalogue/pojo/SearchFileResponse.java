package com.capgemini.searchcatalogue.pojo;

import java.util.List;

import com.capgemini.searchcatalogue.dto.FileResponse;
import com.capgemini.searchcatalogue.dto.Word;
import com.capgemini.searchcatalogue.dto.XmlResponse;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class SearchFileResponse {

	private Integer projectId;
	
	private String projectName;
	
	private String description;
	
	private String mainbranch;

	private String giturl;
	
	@Nullable
	private List<Word> contractFile;
	
	@Nullable
	private List<XmlResponse> pomData;
	
	@Nullable
	private List<FileResponse> searchContent;
	
}