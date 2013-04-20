package no.ntnu.tdt4215.group7.service;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.MedDocument;
import no.ntnu.tdt4215.group7.entity.Sentence;

/**
 * 
 * @author Simon Stastny
 *
 */
public class MatchingServiceImpl implements MatchingService {

	List<MedDocument> book;

	/* (non-Javadoc)
	 * @see no.ntnu.tdt4215.group7.impl.MatchingServicee#findRelevantDocument(no.ntnu.tdt4215.group7.entity.MedDocument)
	 */
	@Override
	public List<MedDocument> findRelevantDocuments(MedDocument input) {

		List<MedDocument> results = new ArrayList<MedDocument>();

		// for each sentence from the input document
		for (Sentence inputSentence : input.getSentences()) {
			// go through all chapters of the legemiddelhåndboka (LMBH)
			for (MedDocument chapter : book) {
				
				List<Sentence> relevantSentences = new ArrayList<Sentence>();
				
				// go through all sentences in this chapter
				for (Sentence sentence : chapter.getSentences()) {
					// if any of the sentence matches the input sentence
					if (sentence.hasMmatch(inputSentence)) {
						// add it to list of relevant sentences
						Sentence s = new Sentence();
						s.setText(sentence.getText());
						
						s.addAllCodes(CodeType.ICD10, sentence.getMatchingCodes(inputSentence, CodeType.ICD10));
						s.addAllCodes(CodeType.ATC, sentence.getMatchingCodes(inputSentence, CodeType.ATC));
						
						relevantSentences.add(s);
					}
				}

				// if we found any hits
				if (!relevantSentences.isEmpty()) {
					MedDocument relevantDoc = new MedDocument(CodeType.LMHB);
					
					// add sentences and chapter id to result document
					relevantDoc.setId(chapter.getId());
					relevantDoc.getSentences().addAll(relevantSentences);

					// put the document to results
					results.add(relevantDoc);
					
					// add chapter id to input MedDoc's relevant ids
					input.getRelevantIds().add(chapter.getId());
				}
			}
		}

		return results;
	}
	
	/**
	 * 
	 * @param book book chapters to look into
	 */
	public MatchingServiceImpl(List<MedDocument> book) {
		super();
		this.book = book;
	}
}
