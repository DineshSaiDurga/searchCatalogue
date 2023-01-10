package com.capgemini.searchcatalogue.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Component;

import com.capgemini.searchcatalogue.pojo.SearchFileResponse;

@Component
public interface SearchFilesFromAllProjectService {

	List<SearchFileResponse> fetchFileData(String path, String keyword) throws InvalidFormatException, IOException, XmlPullParserException;

}
