package com.capgemini.searchcatalogue.dto;

import java.util.List;

import lombok.Data;

@Data
public class Word {

	private String docName;
	
	private List<Head> heads;
}