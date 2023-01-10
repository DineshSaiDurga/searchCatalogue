package com.capgemini.searchcatalogue.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.capgemini.searchcatalogue.config.SearchCatalogueConfiguration;
import com.capgemini.searchcatalogue.dto.Tree;
import com.capgemini.searchcatalogue.model.GitDetails;
import com.capgemini.searchcatalogue.pojo.SearchProjectResponse;
import com.capgemini.searchcatalogue.repository.GitRepository;
import com.capgemini.searchcatalogue.service.SearchProjectService;
import com.capgemini.searchcatalogue.util.SearchUtility;


@Service
@SuppressWarnings("rawtypes")
public class SearchProjectServiceImpl implements SearchProjectService {

	@Autowired
	GitRepository gitrepo;
	
	@Autowired
	SearchUtility utility;

	@Autowired
	SearchCatalogueConfiguration searchCatalogue;
	
	@Override
	public List<SearchProjectResponse> fetchGitData(String domain, String platformName, String services) {
		List<GitDetails> list = gitrepo.findAll();
		List<SearchProjectResponse> mapList = new ArrayList<>();
		for (GitDetails data : list) {			
			HttpEntity<String> entity = utility.setEntity(data.getToken());
			ResponseEntity<Map[]> projectresponse = searchCatalogue.fetchPrivateProjects(services, data.getGiturl(), entity);
			List<Integer> str = new ArrayList<>();
			for (Map tree : projectresponse.getBody()) {
				str.add(Integer.parseInt(tree.get("id").toString()));
			}
			for (Integer n : str) {
				SearchProjectResponse map=filteringProjects(n, data, entity, domain, platformName,services);
				if(map!=null) {
					mapList.add(map);
				}
			}
		}
		return mapList;
	}

	private SearchProjectResponse filteringProjects(Integer n, GitDetails data, HttpEntity<String> entity,
			String domain, String platformName, String services) {
		SearchProjectResponse resp =new SearchProjectResponse();
		ResponseEntity<Map> urlresposne = searchCatalogue.fetchByProjectId(n, data.getGiturl(), entity);
		String mainbranch=urlresposne.getBody().get("web_url").toString();
		String giturl=data.getGiturl().replace("/api/v4/projects", "");
		List<Tree> treelist = searchCatalogue.fetchAlltreeList(data.getGiturl(),entity, n);		
		for (Tree tree : treelist) {
			if (tree.getName().contains("README.md")) {
				String base64text = searchCatalogue.fetchContentFromFile(n, data.getGiturl(), entity, tree.getId());
				String readmeData = utility.convertContentToDecodeString(base64text);
				String[] read = readmeData.split("\\R");
				String dName = null, pName = null;
				for (String dr : read) {					
					if (dr.contains("Domain:")) {
						String[] domainName = dr.split(":");
						dName=domainName[1].strip();
					}
					if (dr.contains("Platform:")) {
						String[] platform = dr.split(":");
						pName=platform[1].strip();
					}
				}
				resp=	fetchReadMeContent(n, domain,platformName, services,mainbranch, giturl, read, dName, pName);
			}
		}
		return resp;
	}

	private SearchProjectResponse fetchReadMeContent(Integer n, String domain, String platformName, String services,
			String mainbranch, String giturl, String[] read, String dName, String pName) {
		SearchProjectResponse res=new SearchProjectResponse();
		for (String dr : read) {
				if (domain.equals(dName) && platformName.equals(pName)) {
					res.setDomain(domain);
					res.setPlatform(platformName);
					res.setService(services);
					res.setProjectId(n.toString());
					res.setGitLink(giturl);
					res.setMainBranch(mainbranch);
					if (dr.contains("Product Description:")) {
						res.setProductDescription(dr.split(":")[1].strip());
					}
					if (dr.contains("Product Name:")) {
						res.setProductName(dr.split(":")[1].strip());
					}
					if (dr.contains("Product Version:")) {
						res.setProductVersion(dr.split(":")[1].strip());
					}
					if (dr.contains("Last Deployment Date:")) {
						res.setLastDeploymentDate(dr.split(":")[1].strip());
					}
				}					
		}
		return res;
	}

	

}
