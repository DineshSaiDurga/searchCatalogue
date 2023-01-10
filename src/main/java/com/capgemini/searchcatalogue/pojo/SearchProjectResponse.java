package com.capgemini.searchcatalogue.pojo;

import lombok.Data;

@Data
public class SearchProjectResponse {

	private String domain;

	private String platform;

	private String service;
	
	private String projectId;

	private String gitLink;

	private String mainBranch;
	
	private String productDescription;

	private String productName;

	private String lastDeploymentDate;

	private String productVersion;

}