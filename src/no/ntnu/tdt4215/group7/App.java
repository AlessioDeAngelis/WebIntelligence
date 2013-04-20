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
import no.ntnu.tdt4215.group7.parser.GoldStandardParser;
import no.ntnu.tdt4215.group7.parser.ICDParser;
import no.ntnu.tdt4215.group7.parser.PatientCaseParser;
import no.ntnu.tdt4215.group7.service.FileService;
import no.ntnu.tdt4215.group7.service.FileServiceImpl;
import no.ntnu.tdt4215.group7.service.MatchingService;
import no.ntnu.tdt4215.group7.service.MatchingServiceImpl;
import no.ntnu.tdt4215.group7.utils.Paths;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;

public class App implements Runnable {
	
	static Logger logger = Logger.getLogger(App.class);
	
	// CODE PARSERS
	final ICDParser icdParser;
	final ATCParser atcParser;

	// FILE SERVICE
	FileService fileService;

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

	private MedDocument goldStandard;

	public void run() {
		long start = System.currentTimeMillis();
		
		// ICD and ATC indexers
		Future<Directory> icdIndexerTask = null;
		Future<Directory> atcIndexerTask = null;
		
		try {
			icdIndexerTask = executor.submit(new ICDIndexer(Paths.ICD10_INDEX_DIRECTORY,icdParser.parseICD(Paths.ICD10_FILE)));
			atcIndexerTask = executor.submit(new ATCIndexer(Paths.ATC_INDEX_DIRECTORY,atcParser.parseATC(Paths.ATC_FILE))); // TODO IMPLEMENTATION
		} catch (IOException e) {
			logger.error(e.getStackTrace());
		}
		
		System.out.println("Indices submited for parsing and indexing execution. " + (System.currentTimeMillis() - start)/1000);

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
		
		System.out.println("Patient case files submited for parsing execution. " + (System.currentTimeMillis() - start)/1000);
			
		// parse all book chapters
		for (String file : bookFileList) {
			completionService.submit(new BookParser(file));
		}
		

		System.out.println("Book files submited for parsing execution. " + (System.currentTimeMillis() - start)/1000);
		
		// parse the gold standard
		
		completionService.submit(new GoldStandardParser(Paths.GOLD_STANDARD_FILE));
		
		System.out.println("Gold standard submitted for parsing execution.  " + (System.currentTimeMillis() - start)/1000);

		try {
			// wait for ICD/ATC indexing
			icdIndex = icdIndexerTask.get();
			atcIndex = atcIndexerTask.get();

			System.out.println("Indices ready. " + (System.currentTimeMillis() - start)/1000);
			
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
			
			System.out.println("Executor closed. " + (System.currentTimeMillis() - start)/1000);
			
			while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
				System.out.println("Awaiting termination. " + (System.currentTimeMillis() - start)/1000);
			}
			
			System.out.println("All MedDocs completed. " + (System.currentTimeMillis() - start)/1000);

		} catch (InterruptedException e) {
			logger.error(e.getStackTrace());
			e.printStackTrace();
		} catch (ExecutionException e) {
			logger.error(e.getStackTrace());
		}

		// use matching service to find relevant documents
		
		try {
			writeOutput();
		} catch (IOException e) {
			logger.error(e.getStackTrace());
		}
		
		// evaluate against the gold standard
		
		
		System.out.println("Evaluation done. " + (System.currentTimeMillis() - start)/1000);
		
		System.out.println("Total duration: " + (System.currentTimeMillis() - start)/1000);
	}

	private void saveDocument(MedDocument doc) {
		if (doc.getType() == CodeType.CLINICAL_NOTE) {
			patientCases.add(doc);
		} else if (doc.getType() == CodeType.CLINICAL_NOTE) {
			book.add(doc);
		} else {
			goldStandard = doc;
		}
	}

	public App() {
		fileService = new FileServiceImpl();
		icdParser = new ICDParser();
		atcParser = new ATCParser();
		atcQueryEngine = new ATCQueryEngine();
		icdQueryEngine = new ICDQueryEngine();
	}

	public void writeOutput() throws IOException {
		
		MatchingService matchingService = new MatchingServiceImpl(book);
		
		for (MedDocument note : patientCases) {
			
			File file = new File(Paths.OUTPUT_DIRECTORY + String.format(Paths.OUTPUT_CASE_FILE_MASK, note.getId()));
			
			logger.info("Writing file: " + file.getName());
			
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
			
			List<MedDocument> relevant = matchingService.findRelevantDocument(note);
			
			for (MedDocument chapt : relevant) {
				bw.write(chapt.toString());
			}
			
			bw.write("</relevant>");
			bw.write("</note>");
			
			bw.close();
		}
		
		System.out.println("Written " + patientCases.size() + " files to data/output/");
	}
	
	public static void main(String[] args) {
		
		App app = new App();
		
		Thread d = new Thread(app);
		
		d.start();
		
		try {
			d.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
