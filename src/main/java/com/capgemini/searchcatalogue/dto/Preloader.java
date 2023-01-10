package com.capgemini.searchcatalogue.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Preloader {
	
	private String projectId;
	
	private List<Tree> trees;
	
	private String giturl;
	
	private String token;
}
