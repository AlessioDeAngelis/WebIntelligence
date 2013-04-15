package no.ntnu.tdt4215.group7;

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
import no.ntnu.tdt4215.group7.entity.Sentence;
import no.ntnu.tdt4215.group7.indexer.OwlIndexer;
import no.ntnu.tdt4215.group7.lookup.CodeAssigner;
import no.ntnu.tdt4215.group7.lookup.QueryEngine;
import no.ntnu.tdt4215.group7.parser.BookParser;
import no.ntnu.tdt4215.group7.parser.PatientCaseParser;
import no.ntnu.tdt4215.group7.service.MatchingServiceImpl;
import no.ntnu.tdt4215.group7.service.FileService;
import no.ntnu.tdt4215.group7.service.FileServiceImpl;
import no.ntnu.tdt4215.group7.service.MatchingService;

import org.apache.lucene.store.Directory;

public class App implements Runnable {

	// FILE SERVICE
	FileService fileService;

	// MATCHING SERVICE
	MatchingService matchingService;

	// QUERY ENGINE
	QueryEngine queryEngine;

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
		Future<Directory> icdIndexerTask = executor.submit(new OwlIndexer(MainApplication.ICD10_FILE)); // TODO IMPLEMENTATION
		Future<Directory> atcIndexerTask = executor.submit(new OwlIndexer(MainApplication.ATC_FILE)); // TODO IMPLEMENTATION

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
					executor.submit(new CodeAssigner(doc, icdIndex, atcIndex, queryEngine));
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
	}

	public void print() {
		for (MedDocument note : patientCases) {
			System.out.println(note);
			
			List<MedDocument> relevant = matchingService.findRelevantDocument(note);
			
			for (MedDocument chapt : relevant) {
				System.out.println(chapt);
			}
		}
	}

}
