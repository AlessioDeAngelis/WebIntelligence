package no.ntnu.tdt4215.group7.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.MedDocument;

public class BookParser implements DocumentParser {
	
	String filename;
	List<MedDocument> results;

	public static void main(String[] args) throws Exception{

		/** 
		 * Test for the 2 Folders of the LMHB
		 */
		String basePath="C:\\Users\\hengsti\\Dropbox\\uni\\a related stuff\\ausland\\A TDT4215 Web intelligence\\Project\\NLH-html-20130123-01";
		String pathL = basePath + "\\L\\";
		List<MedDocument> listL = parseDirectory(pathL);
		String pathT = basePath + "\\T\\"; 
		List<MedDocument> listT = parseDirectory(pathT);
		
		
		
//		/**
//		 *  Test for one specific file
//		 */
//		BookParser bookParser = new BookParser("C:\\Users\\hengsti\\Dropbox\\uni\\a related stuff\\ausland\\A TDT4215 Web intelligence\\Project\\NLH-html-20130123-01\\T\\T1.1.htm");
//		List<MedDocument> list = bookParser.call();
//		for(MedDocument doc: list){
//			System.out.println("ID:" +doc.getId());
//			for(Sentence s: doc.getSentences()){
//				System.out.println("Sentence:" +s);	
//			}
//		}
	}


	public static List<MedDocument> parseDirectory(String path) throws Exception {
		List<MedDocument> masterList = new ArrayList<MedDocument>();
		File[] fileListL = new File(path).listFiles();
		int cnt=0;
		  for(File file: fileListL){
		    if(file.getName().matches("^[T|L]\\d.*")&& file.isFile()){
		    	cnt++;
		    	System.out.println(path+file.getName());
		    	BookParser bookParser = new BookParser(path+file.getName());
		    	masterList.addAll(bookParser.call());
		    }
		  }
		System.out.println(cnt +" Files loaded from "+path);  
		return masterList;
	}
	
	
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
		
		MedDocument currentCase;
		private boolean inHeadline=false;
		private boolean inSentence=false;
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if(qName.equalsIgnoreCase("h2")||qName.equalsIgnoreCase("h3")||qName.equalsIgnoreCase("h4")){
				inSentence = false;
				inHeadline = true; // -->start read heading
				currentCase = new MedDocument(CodeType.LMHB);
				results.add(currentCase);
			}else if(qName.equalsIgnoreCase("footer")){
				// --> stop parsing
				inHeadline=false;
				inSentence=false;
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equalsIgnoreCase("h2")||qName.equalsIgnoreCase("h3")||qName.equalsIgnoreCase("h4")) {
				inHeadline = false; // --> stop read heading
				inSentence = true; // --> start read body
			}
	}
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if(inHeadline){
				String heading = new String(ch, start, length);
				String[] names = heading.split("&nbsp;");
				// only set the id if it start with L or T plus a digit
				if (names[0].matches("^[T|L]\\d.*")) { 
					currentCase.setId(names[0]);
				}
				currentCase.addSentence(heading);
				//System.out.println("<head>"+heading+"</head>");
			}else if (inSentence && length >2) {
				String sentence = new String(ch, start, length);
				//System.out.println("<sent>"+sentence+"</sent>");
				currentCase.addSentence(sentence);
			}
			}
		}
}
