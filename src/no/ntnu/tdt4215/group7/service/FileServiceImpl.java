package no.ntnu.tdt4215.group7.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import no.ntnu.tdt4215.group7.entity.EvaluationResult;
import no.ntnu.tdt4215.group7.entity.MedDocument;
import no.ntnu.tdt4215.group7.utils.Paths;

public class FileServiceImpl implements FileService {

	static Logger logger = Logger.getLogger(FileServiceImpl.class);

	@Override
	public List<String> getBookFiles() {

		List<String> masterList = new ArrayList<String>();

		String path = null;

		String simonLoc = "FIUXMEC:\\Users\\Simon\\Dropbox\\NTNU\\WEB_INTELLIGENCE\\PROJECT\\NLH-html-20130123-01";
		//String martinLoc = "C:\\Users\\hengsti\\Dropbox\\uni\\a related stuff\\ausland\\A TDT4215 Web intelligence\\Project\\NLH-html-20130123-01";
		String alessioLoc = "/home/alessio/Scrivania/NLH-html-20130123-01";

		if (new File(simonLoc).exists()) {
			path = simonLoc;
		} else if (new File(alessioLoc).exists()) {
			path = alessioLoc;
		} else 
//			if (new File(martinLoc).exists()) {
//			path = martinLoc;
//		} else 
			if (new File(Paths.LMHB_DIR).exists()) {
			path = Paths.LMHB_DIR;
		} else if (new File("data/NLH-html-20130123-01").exists()) {
			path = "data/NLH-html-20130123-01";
		} else if (new File("data/book/NLH-html-20130123-01").exists()) {
			path = "data/book/NLH-html-20130123-01";
		} else {
			throw new RuntimeException("Book files not found in any of known locations.");
		}

		File[] fileListL = new File(path + "/L/").listFiles();

		logger.info(fileListL.length + " Files found at " + path);

		int cnt = 0;
		for (File file : fileListL) {
			if (file.getName().matches("^[T|L]\\d.*") && file.isFile()) {
				cnt++;
				masterList.add(path + "/L/" + file.getName());
			}
		}

		logger.info(cnt + " Files loaded from " + path);

		File[] fileListT = new File(path + "/T/").listFiles();

		logger.info(fileListT.length + " Files found at " + path);

		cnt = 0;
		for (File file : fileListT) {
			if (file.getName().matches("^[T|L]\\d.*") && file.isFile()) {
				cnt++;
				masterList.add(path + "/T/" + file.getName());
			}
		}

		logger.info(cnt + " Files loaded from " + path);
		return masterList;

	}

	@Override
	public List<String> getPatientFiles() {

		List<String> results = new ArrayList<String>();

		File[] fileListL = new File(Paths.PATIENT_DATA_DIR).listFiles();

		for (File file : fileListL) {
			if (file.getName().matches(".*\\.xml$") && file.isFile()) {
				results.add(Paths.PATIENT_DATA_DIR + "/" + file.getName());
			}
		}

		return results;
	}

	@Override
	public void writeResults(List<MedDocument> patientCases, MatchingService matchingService) throws IOException {
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

			List<MedDocument> relevant = matchingService.findRelevantDocuments(note);

			for (MedDocument chapt : relevant) {
				bw.write(chapt.toString());
			}

			bw.write("</relevant>");
			bw.write("</note>");

			bw.close();
		}

		System.out.println("Written " + patientCases.size() + " files to " + Paths.OUTPUT_DIRECTORY);
	}

	@Override
	public void writeEval(List<EvaluationResult> results) throws IOException {
		File file = new File(Paths.OUTPUT_DIRECTORY + Paths.EVALUATION_DOCUMENT);

		logger.info("Writing file: " + file.getName());

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write("<results>\n");
		
		for (EvaluationResult evRes : results) {

			bw.write("	<case id=\"" + evRes.getIdCase() + "\">\n");

			bw.write("		<truePos>" + evRes.getTruePositive() + "</truePos>\n");
			bw.write("		<falseNeg>" + evRes.getFalseNegative() + "</falseNeg>\n");
			bw.write("		<falsePos>" + evRes.getFalsePositive() + "</falsePos>\n");
			
			bw.write("	</case>\n");
		}
		
		bw.write("</results>\n");
		
		bw.close();
	}
}
