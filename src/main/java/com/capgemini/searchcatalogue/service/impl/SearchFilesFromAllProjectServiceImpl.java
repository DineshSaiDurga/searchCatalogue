package com.capgemini.searchcatalogue.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.capgemini.searchcatalogue.config.SearchCatalogueConfiguration;
import com.capgemini.searchcatalogue.dto.FileResponse;
import com.capgemini.searchcatalogue.dto.Head;
import com.capgemini.searchcatalogue.dto.Preloader;
import com.capgemini.searchcatalogue.dto.Tree;
import com.capgemini.searchcatalogue.dto.Word;
import com.capgemini.searchcatalogue.dto.XmlResponse;
import com.capgemini.searchcatalogue.pojo.SearchFileResponse;
import com.capgemini.searchcatalogue.service.SearchFilesFromAllProjectService;
import com.capgemini.searchcatalogue.util.SearchUtility;

@Service
@SuppressWarnings("rawtypes")
public class SearchFilesFromAllProjectServiceImpl implements SearchFilesFromAllProjectService {

	@Autowired
	SearchUtility utility;

	@Autowired
	SearchCatalogueConfiguration searchCatalogue;

	@Override
	public List<SearchFileResponse> fetchFileData(String path, String keyword) throws InvalidFormatException, IOException, XmlPullParserException {

		List<SearchFileResponse> mapList = new ArrayList<>();
		List<Preloader> pre = searchCatalogue.fetchAllDetails();
		for (Preloader prel : pre) {
			HttpEntity<String> entity = utility.setEntity(prel.getToken());
			SearchFileResponse map = filteringProjects(prel, entity, path, keyword);
			if (map.getProjectId() != null) {
				mapList.add(map);
			}
		}
		return mapList;
	}

	private SearchFileResponse filteringProjects(Preloader data, HttpEntity<String> entity, String path, String keyword) throws IOException, InvalidFormatException, XmlPullParserException {
		
		int n = Integer.parseInt(data.getProjectId());
		SearchFileResponse filterProject = new SearchFileResponse();
		List<Word> word = new ArrayList<>();
		List<XmlResponse> re = new ArrayList<>();
		List<FileResponse> mList = new ArrayList<>();
		CopyOnWriteArrayList<FileResponse> li = new CopyOnWriteArrayList<>();
		ResponseEntity<Map> projectresposne = searchCatalogue.fetchProjectInfoById(n, data.getGiturl(), entity);
		String mainbranch = projectresposne.getBody().get("web_url").toString();
		String giturl = data.getGiturl().replace("/api/v4/projects", "");
		String name = projectresposne.getBody().get("name").toString();
		String description = projectresposne.getBody().get("description").toString();
		String fileData = null;
		for (Tree tree : data.getTrees()) {
			String searchkey = null;
			if (path == null || path.isBlank() || path.isEmpty()) {
				searchkey = keyword;
			} else {
				searchkey = path + "/" + keyword;
			}
			if (tree.getPath().contains(searchkey)) {
				String base64text = searchCatalogue.fetchContentFromFile(n, data.getGiturl(), entity, tree.getId());
				FileResponse project = new FileResponse();
				fileData = utility.convertContentToDecodeString(base64text);
				if (tree.getName().contains(".docx") || tree.getName().contains(".doc")) {
					List<Head> hlist = utility.convertDocx(base64text, tree.getName());
					utility.setWord(word, tree, hlist);
				} else if (tree.getName().contains("pom.xml")) {
					InputStream stream = new ByteArrayInputStream(fileData.getBytes(Charset.forName("UTF-8")));
					MavenXpp3Reader pomReader = new MavenXpp3Reader();
					Model model = pomReader.read(stream);
					XmlResponse xml = utility.fetchXMLResponse(model);
					re.add(xml);
				} else {
					utility.setFileData(fileData, tree, project);
					li.addIfAbsent(project);
				}
			}
		}
		mList.addAll(li);

		if (!mList.isEmpty() || !re.isEmpty() || !word.isEmpty()) {
			utility.setResponse(n, filterProject, word, re, mList, mainbranch, giturl, name, description);
		}

		return filterProject;
	}

}
