package no.ntnu.tdt4215.group7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import no.ntnu.tdt4215.group7.entity.PatientCase;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PatientCaseParser implements Callable<List<PatientCase>> {
	List<PatientCase> results;
	String filename;

	@Override
	public List<PatientCase> call() throws Exception {
		results = new ArrayList<PatientCase>();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		saxParser.parse(filename, new PatientXmlHandler());

		return results;
	}

	public PatientCaseParser(String filename) {
		this.filename = filename;
	}
	
	public PatientCaseParser() {
		this("data/cases.xml");
	}

	class PatientXmlHandler extends DefaultHandler {

		PatientCase currentCase;
		boolean inSentence = false;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("case")) {
				currentCase = new PatientCase();
				currentCase.setId(Integer.parseInt(attributes.getValue(0)));
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
