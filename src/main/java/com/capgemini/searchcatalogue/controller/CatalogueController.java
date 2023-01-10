package com.capgemini.searchcatalogue.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.searchcatalogue.dto.Platforms;
import com.capgemini.searchcatalogue.model.UserDetails;
import com.capgemini.searchcatalogue.pojo.SearchFileRequest;
import com.capgemini.searchcatalogue.pojo.SearchFileResponse;
import com.capgemini.searchcatalogue.pojo.SearchProjectFileRequest;
import com.capgemini.searchcatalogue.pojo.SearchProjectRequest;
import com.capgemini.searchcatalogue.pojo.SearchProjectResponse;
import com.capgemini.searchcatalogue.service.LoginService;
import com.capgemini.searchcatalogue.service.SearchFilesByProjectService;
import com.capgemini.searchcatalogue.service.SearchFilesFromAllProjectService;
import com.capgemini.searchcatalogue.service.SearchProjectService;

@RestController
@CrossOrigin
public class CatalogueController {

	@Autowired
	LoginService lservice;
	
	@Autowired
	SearchProjectService searchProjectService;
	
	@Autowired
	SearchFilesFromAllProjectService searchFilesFromAllProjectService;
	
	@Autowired
	SearchFilesByProjectService searchFilesByProjectService;	
	
	@PostMapping("/validate-login")
	public ResponseEntity<UserDetails> validateUser(@RequestBody UserDetails user) {
		return lservice.validateUserCredentials(user.getEmailId(),user.getPassword());
	}
	
	@GetMapping("/search")
	public List<SearchProjectResponse> fetchGitdata(@RequestBody SearchProjectRequest requestData) {

		List<SearchProjectResponse> maplist = new ArrayList<>();
        if ("GIT".equals(requestData.getSource()) || "ALL".equals(requestData.getSource())) {
        	for (Platforms platform : requestData.getPlatform()) {
                for (String services : platform.getServiceName()) {
                	List<SearchProjectResponse> list = searchProjectService.fetchGitData(requestData.getDomain(),
                            platform.getPlatformName(), services);
                    maplist.addAll(list);
                }
            }
        }
        return maplist;
	}
	
	@GetMapping("/fetchAll")
	public List<SearchFileResponse> displayFileContent(@RequestBody SearchFileRequest req)  throws IOException, InvalidFormatException, XmlPullParserException{
		return searchFilesFromAllProjectService.fetchFileData(req.getPath(),req.getKeyword());
	}
	
	@GetMapping("/fetchByProject")
	public SearchFileResponse displayFileContentById(@RequestBody SearchProjectFileRequest req)  throws IOException, InvalidFormatException, XmlPullParserException{
		return searchFilesByProjectService.fetchFileDataById(req);
		
	}
	
}
