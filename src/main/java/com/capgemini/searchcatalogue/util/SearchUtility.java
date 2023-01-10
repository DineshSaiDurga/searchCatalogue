package com.capgemini.searchcatalogue.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.capgemini.searchcatalogue.dto.FileResponse;
import com.capgemini.searchcatalogue.dto.Head;
import com.capgemini.searchcatalogue.dto.Tree;
import com.capgemini.searchcatalogue.dto.Word;
import com.capgemini.searchcatalogue.dto.XmlResponse;
import com.capgemini.searchcatalogue.pojo.SearchFileResponse;

@Component
public class SearchUtility {

	@Value("${search.catalogue.filepath}")
	private String path;

	public HttpEntity<String> setEntity(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/vnd.github+json");
		headers.setBearerAuth(token);
		return new HttpEntity<>("header", headers);
	}

	public List<Head> convertDocx(String base64text, String name) throws InvalidFormatException, IOException {
		deleteFile(path);
		encodeTextToFiles(base64text, name, path);
		decodeFiles(path);
		List<Head> head = fetchHeader(path + "\\" + name);
		List<Head> hlist = fetchParagraphFromHeaders(head, name, path);
		deleteFile(path);
		return hlist;
	}

	private void encodeTextToFiles(String base64text, String name, String path) throws IOException {
		try (FileWriter writer = new FileWriter(path + "\\" + name.replace(".docx", ".txt"))) {
			writer.write(base64text);
		}
	}

	private void decodeFiles(String path) throws IOException {
		File dir = new File(path);
		String[] names = dir.list();
		for (String file : names) {
			if (file.contains(".txt")) {
				Path input = Paths.get(path + "\\" + file);
				Path out = Paths.get(path + "\\" + file.replace(".txt", ".docx"));
				try (InputStream in = Base64.getDecoder().wrap(Files.newInputStream(input))) {
					Files.copy(in, out);
				}
			}
		}
	}

	private List<Head> fetchHeader(String path) throws InvalidFormatException, IOException {
		List<Head> list = new ArrayList<>();
		FileInputStream fis = new FileInputStream(path);
		XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
		XWPFStyles styles = xdoc.getStyles();
		List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
		int para = 0;
		for (XWPFParagraph paragraph : paragraphList) {
			if (paragraph.getStyleID() != null) {
				XWPFStyle style = styles.getStyle(paragraph.getStyleID());
				if (style != null) {
					if (style.getName().startsWith("heading")) {
						if (!paragraph.getText().isBlank()) {
							Head h = new Head();
							h.setHeading(paragraph.getText());
							h.setLineno(para);
							list.add(h);
						}
					}
				}
			}
			para++;
		}
		return list;
	}

	private List<Head> fetchParagraphFromHeaders(List<Head> head, String name, String path)
			throws InvalidFormatException, IOException {
		List<Head> hlist = new ArrayList<>();
		File dir = new File(path + "\\" + name);
		FileInputStream fis = new FileInputStream(dir);
		XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
		List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
		for (int i = 1; i <= head.size(); i++) {
			StringBuilder sb = new StringBuilder();
			Head h = head.get(i - 1);
			int n = 0;
			if (i == head.size()) {
				n = paragraphList.size();
			} else {
				n = head.get(i).getLineno();
			}
			for (int j = h.getLineno() + 1; j < n; j++) {
				String text = "\u2022 " + paragraphList.get(j).getText() + "\n";
				sb.append(text);
			}
			Head hd = new Head();
			hd.setHeading(h.getHeading());
			hd.setLineno(h.getLineno());
			hd.setPara(sb.toString());
			hlist.add(hd);
		}
		return hlist;
	}

	public void deleteFile(String path) throws IOException {
		File dirFile = new File(path);
		FileUtils.cleanDirectory(dirFile);
	}

	public String convertContentToDecodeString(String blobresponse) {
		byte[] decodedBytes = Base64.getDecoder().decode(blobresponse);
		String readmeData = new String(decodedBytes);
		return readmeData;
	}

	public void setFileData(String fileData, Tree tree, FileResponse project) {
		project.setFilename(tree.getName());
		project.setFilepath(tree.getPath());
		project.setFilecontent(fileData);
	}

	public void setWord(List<Word> word, Tree tree, List<Head> hlist) {
		Word w = new Word();
		if (!hlist.isEmpty()) {
			w.setDocName(tree.getName());
			w.setHeads(hlist);
			word.add(w);
		}
	}

	public void setResponse(Integer n, SearchFileResponse filterProject, List<Word> word,
			List<XmlResponse> re, List<FileResponse> mList, String mainbranch, String giturl, String name,
			String description) {
		filterProject.setProjectId(n);
		filterProject.setProjectName(name);
		filterProject.setDescription(description);
		filterProject.setMainbranch(mainbranch);
		filterProject.setGiturl(giturl);
		if (word.size() == 0) {
			filterProject.setContractFile(null);
		} else {
			filterProject.setContractFile(word);
		}
		if (re.size() == 0) {
			filterProject.setPomData(null);
		} else {
			filterProject.setPomData(re);
		}
		if (mList.size() == 0) {
			filterProject.setSearchContent(null);
		} else {
			filterProject.setSearchContent(mList);
		}
	}

	public XmlResponse fetchXMLResponse(Model model) {
		XmlResponse xml = new XmlResponse();
		xml.setName(model.getName());
		xml.setDescripion(model.getDescription());
		xml.setPackaging(model.getPackaging());
		xml.setVersion(model.getVersion());
		if (model.getModules().isEmpty()) {
			xml.setModules(null);
		} else {
			xml.setModules(model.getModules());
		}
		return xml;
	}
}
