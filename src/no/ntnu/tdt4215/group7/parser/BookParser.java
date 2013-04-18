package no.ntnu.tdt4215.group7.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.MedDocument;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BookParser implements DocumentParser {
	
	static Logger log = Logger.getLogger("bp");

	String filename;
	List<MedDocument> results;

	@Override
	public List<MedDocument> call() {
		results = new ArrayList<MedDocument>();

		HtmlParser parser = new HtmlParser();

		FileInputStream fis;
		try {
			fis = new FileInputStream(filename);
			parser.parse(fis, new ChapterHandler(), new Metadata());
		} catch (FileNotFoundException e) {
			log.error(e.getStackTrace());
		} catch (IOException e) {
			log.error(e.getStackTrace());
		} catch (SAXException e) {
			log.error(e.getStackTrace());
		} catch (TikaException e) {
			log.error(e.getStackTrace());
		}

		log.info("Found " + results.size() + " MedDocuments in " + filename);
		
		return results;
	}

	public BookParser(String filename) {
		this.filename = filename;
	}

	class ChapterHandler extends DefaultHandler {

		MedDocument currentCase;
		private boolean inHeadline = false;
		private boolean inSentence = false;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("h2") || qName.equalsIgnoreCase("h3") || qName.equalsIgnoreCase("h4")) {
				inSentence = false;
				inHeadline = true; // -->start read heading
				currentCase = new MedDocument(CodeType.LMHB);
			} else if (qName.equalsIgnoreCase("footer")) {
				// --> stop parsing
				inHeadline = false;
				inSentence = false;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equalsIgnoreCase("h2") || qName.equalsIgnoreCase("h3") || qName.equalsIgnoreCase("h4")) {
				inHeadline = false; // --> stop read heading
				inSentence = true; // --> start read body
				if(currentCase.getSentences().size() > 0) {
					results.add(currentCase);
				}
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (inHeadline) {
				String heading = new String(ch, start, length);
				String[] names = heading.split("&nbsp;");
				// only set the id if it start with L or T plus a digit
				if (names[0].matches("^[T|L]\\d.*")) {
					currentCase.setId(names[0]);
				}
				currentCase.addSentence(heading);
			} else if (inSentence && length > 2) {
				String sentence = new String(ch, start, length);
				currentCase.addSentence(sentence);
			}
		}
	}
}
