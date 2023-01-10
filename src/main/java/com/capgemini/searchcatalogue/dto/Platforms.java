package com.capgemini.searchcatalogue.dto;

import java.util.List;

import lombok.Data;

@Data
public class Platforms {

	private String platformName;

	private List<String> serviceName;

}