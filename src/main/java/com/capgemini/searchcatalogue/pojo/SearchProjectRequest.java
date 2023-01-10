package com.capgemini.searchcatalogue.pojo;

import java.util.List;

import com.capgemini.searchcatalogue.dto.Platforms;

import lombok.Data;

@Data
public class SearchProjectRequest {

	private String domain;

	private List<Platforms> platform;

	private String source;

}
