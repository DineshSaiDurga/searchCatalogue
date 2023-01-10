package com.capgemini.searchcatalogue.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.capgemini.searchcatalogue.pojo.SearchProjectResponse;

@Component
public interface SearchProjectService {

	List<SearchProjectResponse> fetchGitData(String domain, String platformName, String services);

}
