package no.ntnu.tdt4215.group7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ntnu.tdt4215.group7.entity.EvaluationResult;
import no.ntnu.tdt4215.group7.entity.MedDocument;

public class EvaluationService {
	
	final Map<String, MedDocument> patientCases;
	final Map<String, MedDocument> goldStandard;
	
	public EvaluationService(List<MedDocument> patientCasesList, List<MedDocument> goldStandardList) {
		super();
		this.patientCases = new HashMap<String, MedDocument>(patientCasesList.size());
		this.goldStandard = new HashMap<String, MedDocument>(goldStandardList.size());;
		
		for(MedDocument doc : patientCasesList) {
			patientCases.put(doc.getId(), doc);
		}
		
		for(MedDocument doc : goldStandardList) {
			goldStandard.put(doc.getId(), doc);
		}
	}
	
	public List<EvaluationResult> call() {
		List<EvaluationResult> results = new ArrayList<EvaluationResult>();
		
		for(String key : patientCases.keySet()) {
			
			
			
			
		}
		
		
		return results;
	}
}
