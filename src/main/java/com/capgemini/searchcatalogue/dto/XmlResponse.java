package com.capgemini.searchcatalogue.dto;

import java.util.List;

import lombok.Data;

@Data
public class XmlResponse {

	private String name;

	private String descripion;

	private String packaging;
	
	private String version;
	
	private List<String> modules;
	
}