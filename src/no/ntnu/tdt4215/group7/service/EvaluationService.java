package no.ntnu.tdt4215.group7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import no.ntnu.tdt4215.group7.entity.EvaluationResult;
import no.ntnu.tdt4215.group7.entity.MedDocument;

public class EvaluationService implements Callable<List<EvaluationResult>> {
	
	final Map<String, MedDocument> patientCasesCollection;
	final Map<String, MedDocument> goldStandardCollection;
	
	public EvaluationService(List<MedDocument> patientCasesList, List<MedDocument> goldStandardList) {
		super();
		this.patientCasesCollection = new HashMap<String, MedDocument>(patientCasesList.size());
		this.goldStandardCollection = new HashMap<String, MedDocument>(goldStandardList.size());;
		
		for(MedDocument doc : patientCasesList) {
			patientCasesCollection.put(doc.getId(), doc);
		}
		
		for(MedDocument doc : goldStandardList) {
			goldStandardCollection.put(doc.getId(), doc);
		}
	}
	
	public List<EvaluationResult> call() {
		List<EvaluationResult> results = new ArrayList<EvaluationResult>();
		
		for(String key : patientCasesCollection.keySet()) {
			
			MedDocument goldStandard = goldStandardCollection.get(key);
			MedDocument patientCase = patientCasesCollection.get(key);

			int truePos = 0;
			int falsePos = 0;
			int falseNeg = 0;
			
			for(String res : goldStandard.getRelevantIds()) {
				if(patientCase.containsRelevantId(res)) {
					truePos++;
				} else {
					falseNeg++;
				}
			}
			
			falsePos = patientCase.getRelevantIds().size() - truePos;
			
			results.add(new EvaluationResult(key, truePos,falseNeg,falsePos));
		}
		
		return results;
	}
}
