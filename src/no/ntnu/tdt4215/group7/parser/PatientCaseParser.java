package no.ntnu.tdt4215.group7.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.MedDocument;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PatientCaseParser implements DocumentParser {
	
	static Logger log = Logger.getLogger("pp");
	
	List<MedDocument> results;
	String filename;

	@Override
	public List<MedDocument> call() {
		results = new ArrayList<MedDocument>();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(filename, new PatientXmlHandler());
		} catch (ParserConfigurationException e) {
			log.error(e.getStackTrace());
		} catch (SAXException e) {
			log.error(e.getStackTrace());
		} catch (IOException e) {
			log.error(e.getStackTrace());
		}
		
		log.info(results.size() + " patient cases found.");

		return results;
	}

	public PatientCaseParser(String filename) {
		this.filename = filename;
	}

	class PatientXmlHandler extends DefaultHandler {

		MedDocument currentCase;
		boolean inSentence = false;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("case")) {
				currentCase = new MedDocument(CodeType.CLINICAL_NOTE);
				currentCase.setId(attributes.getValue(0));
			} else if (qName.equals("sentence")) {
				inSentence = true;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equals("case")) {
				results.add(currentCase);
			} else if (qName.equals("sentence")) {
				inSentence = false;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (inSentence) {
				String sentence = new String(ch, start, length);
				currentCase.addSentence(sentence);
			}
		}
	}
}
