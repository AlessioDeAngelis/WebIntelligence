package no.ntnu.tdt4215.group7.service;

import java.io.IOException;
import java.util.List;

import no.ntnu.tdt4215.group7.entity.EvaluationResult;
import no.ntnu.tdt4215.group7.entity.MedDocument;

public interface FileService {
	
	public List<String> getBookFiles();

	public List<String> getPatientFiles();
	
	public void writeEval(List<EvaluationResult> results) throws IOException;

	public void writeResults(List<MedDocument> patientCases, MatchingService matchingService) throws IOException;
}
