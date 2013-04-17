package no.ntnu.tdt4215.group7.test;

import java.util.List;

import no.ntnu.tdt4215.group7.entity.MedDocument;
import no.ntnu.tdt4215.group7.parser.BookParser;
import no.ntnu.tdt4215.group7.parser.DocumentParser;
import no.ntnu.tdt4215.group7.parser.PatientCaseParser;
import no.ntnu.tdt4215.group7.service.FileService;
import no.ntnu.tdt4215.group7.service.FileServiceImpl;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

	private FileService fs;
	
	private DocumentParser parser;

	@Before
	public void setUp() {
		fs = new FileServiceImpl();
	}
	
	@Test
	public void testBookParser() throws Exception {
		List<String> files = fs.getBookFiles();

		assertTrue(files.size() > 0);

		for (String file : files) {
			System.out.println(file);
			assertNotNull(file);
			
			parser = new BookParser(file);
			
			List<MedDocument> result = parser.call();
			
			if(result.size()==0) {
				System.out.println(file);
			}

			assertNotNull(result);
			assertTrue(result.size() > 0);
			
			for(MedDocument md : result) {
				assertNotNull(md);
				assertTrue(md.getSentences().size() > 0);
			}
		}
	}
	
	public void testPatientParser() throws Exception {
		List<String> files = fs.getPatientFiles();

		assertTrue(files.size() > 0);

		for (String file : files) {
			System.out.println(file);
			assertNotNull(file);
			
			parser = new PatientCaseParser(file);
			
			List<MedDocument> result = parser.call();
			
			if(result.size()==0) {
				System.out.println(file);
			}
			
			assertNotNull(result);
			assertTrue(result.size() > 0);
			
			for(MedDocument md : result) {
				assertNotNull(md);
				assertTrue(md.getSentences().size() > 0);
			}
		}
	}
}
