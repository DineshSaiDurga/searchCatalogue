package com.capgemini.searchcatalogue.service;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Component;

import com.capgemini.searchcatalogue.pojo.SearchFileResponse;
import com.capgemini.searchcatalogue.pojo.SearchProjectFileRequest;

@Component
public interface SearchFilesByProjectService {

	SearchFileResponse fetchFileDataById(SearchProjectFileRequest req) throws IOException, XmlPullParserException, InvalidFormatException;

}
