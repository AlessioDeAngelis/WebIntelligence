package no.ntnu.tdt4215.group7.test;

import java.util.List;

import junit.framework.TestCase;

import no.ntnu.tdt4215.group7.service.FileService;
import no.ntnu.tdt4215.group7.service.FileServiceImpl;

import org.junit.Before;
import org.junit.Test;

public class FileTest extends TestCase {

	private FileService fs;

	@Before
	public void setUp() {
		fs = new FileServiceImpl();
	}

	@Test
	public void testPatientFiles() {
		List<String> files = fs.getPatientFiles();

		assertTrue(files.size() > 0);

		for (String file : files) {
			System.out.println(file);
			assertNotNull(file);
		}
	}

	@Test
	public void testBookFiles() {
		List<String> files = fs.getBookFiles();

		assertTrue(files.size() == 323);
		
		System.out.print("book files found : " + files.size());

		for (String file : files) {
			assertNotNull(file);
		}
	}
}
