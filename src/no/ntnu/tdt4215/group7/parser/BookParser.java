package no.ntnu.tdt4215.group7.parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.xml.sax.helpers.DefaultHandler;


import no.ntnu.tdt4215.group7.entity.MedDocument;

public class BookParser implements DocumentParser {
	
	String filename;
	List<MedDocument> results;

	@Override
	public List<MedDocument> call() throws Exception {
		results = new ArrayList<MedDocument>();

		HtmlParser parser = new HtmlParser();
		
		FileInputStream fis = new FileInputStream(filename);

		parser.parse(fis, new ChapterHandler(), new Metadata());

		return results;
	}
	
	public BookParser(String filename) {
		this.filename = filename;
	}
	
	class ChapterHandler extends DefaultHandler {
		//FIXME
	}
}
