package no.ntnu.tdt4215.group7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.MedDocument;
import no.ntnu.tdt4215.group7.indexer.ATCIndexer;
import no.ntnu.tdt4215.group7.indexer.ICDIndexer;
import no.ntnu.tdt4215.group7.lookup.ATCQueryEngine;
import no.ntnu.tdt4215.group7.lookup.CodeAssigner;
import no.ntnu.tdt4215.group7.lookup.ICDQueryEngine;
import no.ntnu.tdt4215.group7.lookup.QueryEngine;
import no.ntnu.tdt4215.group7.parser.ATCParser;
import no.ntnu.tdt4215.group7.parser.BookParser;
import no.ntnu.tdt4215.group7.parser.ICDParser;
import no.ntnu.tdt4215.group7.parser.PatientCaseParser;
import no.ntnu.tdt4215.group7.service.FileService;
import no.ntnu.tdt4215.group7.service.FileServiceImpl;
import no.ntnu.tdt4215.group7.service.MatchingService;
import no.ntnu.tdt4215.group7.service.MatchingServiceImpl;
import no.ntnu.tdt4215.group7.utils.Paths;

import org.apache.lucene.store.Directory;

import com.cedarsoftware.util.io.JsonWriter;

public class App implements Runnable {
	
	// CODE PARSERS
	final ICDParser icdParser;
	final ATCParser atcParser;

	// FILE SERVICE
	FileService fileService;

	// MATCHING SERVICE
	MatchingService matchingService;

	// QUERY ENGINE
	final QueryEngine atcQueryEngine;
	final QueryEngine icdQueryEngine;

	// indices
	Directory icdIndex;
	Directory atcIndex;

	// parsed documents
	List<MedDocument> book = new ArrayList<MedDocument>();
	List<MedDocument> patientCases = new ArrayList<MedDocument>();

	private ExecutorService executor = Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors());

	public void run() {
		// ICD and ATC indexers
		Future<Directory> icdIndexerTask = null;
		Future<Directory> atcIndexerTask = null;
		
		try {
			icdIndexerTask = executor.submit(new ICDIndexer(Paths.ICD10_INDEX_DIRECTORY,icdParser.parseICD(Paths.ICD10_FILE)));
			atcIndexerTask = executor.submit(new ATCIndexer(Paths.ATC_INDEX_DIRECTORY,atcParser.parseATC(Paths.ATC_FILE))); // TODO IMPLEMENTATION
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		// PATIENT FILES
		List<String> patientFileList = fileService.getPatientFiles();

		// LEGEMIDDELHÅNDBOK
		List<String> bookFileList = fileService.getBookFiles();

		CompletionService<List<MedDocument>> completionService = new ExecutorCompletionService<List<MedDocument>>(
				executor);

		// parse all patient case files
		for (String file : patientFileList) {
			completionService.submit(new PatientCaseParser(file));
		}

		// parse all book chapters
		for (String file : bookFileList) {
			completionService.submit(new BookParser(file));
		}

		try {
			// wait for ICD/ATC indexing
			icdIndex = icdIndexerTask.get();
			atcIndex = atcIndexerTask.get();

			// get ready MedDocs
			for (int i = 0; i < (patientFileList.size() + bookFileList.size()); i++) {

				Future<List<MedDocument>> result;

				result = completionService.take();

				for (MedDocument doc : result.get()) {
					// save document to collection
					saveDocument(doc);

					// assign ICD and ATC codes to the document
					executor.submit(new CodeAssigner(doc, icdIndex, atcIndex, icdQueryEngine, atcQueryEngine));
				}
			}

			// shutdown the thread pool and await termination
			executor.shutdown();

			while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
				System.out.println("Awaiting thread pool termination");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// use matching service to find relevant documents
		
		matchingService = new MatchingServiceImpl(book);
		
		try {
			print();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveDocument(MedDocument doc) {
		if (doc.getType() == CodeType.CLINICAL_NOTE) {
			patientCases.add(doc);
		} else {
			book.add(doc);
		}
	}

	public App() {
		fileService = new FileServiceImpl();
		icdParser = new ICDParser();
		atcParser = new ATCParser();
		atcQueryEngine = new ATCQueryEngine();
		icdQueryEngine = new ICDQueryEngine();
	}

	public void print() throws IOException {
		for (MedDocument note : patientCases) {
			
			File file = new File("data/output/" + note.getType() + "_" + note.getId() + ".txt");
			
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("<note>");
			bw.write("<case>");
			bw.write(note.toString());
			bw.write("</case>");
			bw.write("<relevant>");
			
			//System.out.println(note);
			
			List<MedDocument> relevant = matchingService.findRelevantDocument(note);
			
			for (MedDocument chapt : relevant) {
				//System.out.println(chapt);
				bw.write(chapt.toString());
			}
			
			bw.write("</relevant>");
			bw.write("</note>");
			
			bw.close();
		}
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		App app = new App();
		
		Thread d = new Thread(app);
		
		d.start();
		
		try {
			d.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("dur: " + (System.currentTimeMillis() - start)/1000);
	}

}
