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

	static Logger log = Logger.getLogger("BookParser");

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

		MedDocument currentChapter;
		private boolean inHeadline = false;
		private boolean inSentence = false;
		private boolean parsingEnabled = true;

		private StringBuffer headingBuffer;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

				if (qName.equalsIgnoreCase("h1") || qName.equalsIgnoreCase("h2") || qName.equalsIgnoreCase("h3")
						|| qName.equalsIgnoreCase("h4")) {
					inSentence = false;
					inHeadline = true; // -->start read heading
					currentChapter = new MedDocument(CodeType.LMHB);
					headingBuffer = new StringBuffer();
				}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			
				if (qName.equalsIgnoreCase("h1") || qName.equalsIgnoreCase("h2") || qName.equalsIgnoreCase("h3")
						|| qName.equalsIgnoreCase("h4")) {
					if(parsingEnabled){
						currentChapter.addSentence(headingBuffer.toString());
					}
					inHeadline = false; // --> stop read heading
					inSentence = true; // --> start read body
					parsingEnabled=true;
					if (currentChapter.getSentences().size() > 0) {
						currentChapter.setId(headingBuffer.toString().replaceAll("\\s{2,}", " ").trim());
						results.add(currentChapter);
					} else {
						log.warn("Empty MedDoc skipped. Id: " + currentChapter.getId());
					}
				}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(parsingEnabled){	
				String sentence = new String(ch, start, length);
				if (inHeadline && length > 1) {
					String corrected = sentence.replaceAll("[^a-zA-Z0-9������\\s\\.]+", "");
					headingBuffer.append(corrected);
					// if first element of heading 
					if (headingBuffer.length() == corrected.length()) {
						// if not legal heading (stats with T|L followed by a digit)
						if(!headingBuffer.toString().matches("^[TL]\\d.*")){
							parsingEnabled=false;
						}
						headingBuffer.append(" "); //add a space
					}

				} else if (inSentence && length > 2) { 
					currentChapter.addSentence(sentence.replaceAll("[^a-zA-Z0-9������\\s\\.]+", "").trim());
				}
			}
		}
	}
}
