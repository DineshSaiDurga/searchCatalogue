package com.capgemini.searchcatalogue.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.capgemini.searchcatalogue.dto.Preloader;
import com.capgemini.searchcatalogue.dto.Tree;
import com.capgemini.searchcatalogue.model.GitDetails;
import com.capgemini.searchcatalogue.repository.GitRepository;
import com.capgemini.searchcatalogue.util.SearchUtility;

@Configuration
@SuppressWarnings("rawtypes")
public class SearchCatalogueConfiguration {

	@Autowired
	SearchUtility utility;
	
	@Autowired
	GitRepository gitrepo;
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
		
	public ResponseEntity<Map> fetchProjectInfoById(Integer n, String url, HttpEntity<String> entity) {
		ResponseEntity<Map> projectresposne = restTemplate().exchange(url + "/" + n, HttpMethod.GET, entity, Map.class);
		return projectresposne;
	}
	
	@Bean
	@DependsOn("restTemplate")
	public List<Preloader> fetchAllDetails(){
		List<Preloader> list = new ArrayList<>();
		List<GitDetails> data= gitrepo.findAll();
		for(GitDetails git:data) {
			HttpEntity<String> entity = utility.setEntity(git.getToken());
			ResponseEntity<Map[]> projectresponse = fetchprojects(git.getGiturl(), entity);
			List<Integer> str = new ArrayList<>();
			for (Map<?, ?> project : projectresponse.getBody()) {
				str.add(Integer.parseInt(project.get("id").toString()));
			}
			for (Integer n : str) {
				List<Tree> trees=fetchAlltreeList(git.getGiturl(),entity,n);
				Preloader pre =new Preloader(String.valueOf(n),trees,git.getGiturl(),git.getToken());
					list.add(pre);
			}
		}
		return list;
	}
	
	
	
	public List<Tree> fetchAlltreeList(String giturl, HttpEntity<String> entity, Integer n) {
		List<Tree> treelist = new ArrayList<>();
		Integer no=0;
		for (int pageno = 1;pageno<=1000; pageno++) {
			ResponseEntity<Tree[]> treeresponse = restTemplate().exchange(
					giturl + "/" + n + "/repository/tree?ref=master&recursive=true&per_page=100&page=" + pageno,
					HttpMethod.GET, entity, Tree[].class);
			if(treeresponse.getBody().length==0) {
				no=pageno;
			}		
			if(pageno==no) {
				break;
			}
			List<Tree> tree = Arrays.asList(treeresponse.getBody());
			treelist.addAll(tree);
		}
		return treelist;
	}

	public String fetchContentFromFile(Integer n, String url, HttpEntity<String> entity, String id) {
		ResponseEntity<Map> blobresponse = restTemplate().exchange(url + "/" + n + "/repository/blobs/" + id, HttpMethod.GET,
				entity, Map.class);
		String base64text = blobresponse.getBody().get("content").toString();
		return base64text;
	}

	public ResponseEntity<Map[]> fetchprojects(String url, HttpEntity<String> entity) {
		return restTemplate().exchange(url + "?visibility=private", HttpMethod.GET, entity, Map[].class);
	}

	public ResponseEntity<Map[]> fetchPrivateProjects(String services, String url, HttpEntity<String> entity) {
		return restTemplate().exchange(url + "?visibility=private&search=" + services, HttpMethod.GET, entity,
				Map[].class);
	}
	public ResponseEntity<Map> fetchByProjectId(Integer n, String url, HttpEntity<String> entity) {
		return restTemplate().exchange(url + "/" + n, HttpMethod.GET, entity, Map.class);
	}

}
