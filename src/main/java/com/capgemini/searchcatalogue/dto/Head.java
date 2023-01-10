package com.capgemini.searchcatalogue.dto;

import jakarta.persistence.Column;
import lombok.Data;


@Data
public class Head {
	
	private String heading;
	
	private Integer lineno;
	
	@Column(nullable = true)
	private String para;
	
}