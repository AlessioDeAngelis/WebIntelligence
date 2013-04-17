package no.ntnu.tdt4215.group7.lookup;

import java.util.List;

import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.MedDocument;
import no.ntnu.tdt4215.group7.entity.Sentence;

import org.apache.lucene.store.Directory;

public class CodeAssigner implements Runnable {
	final QueryEngine atcQueryEngine;
	final QueryEngine icdQueryEngine;
	final MedDocument doc;
	final Directory icdIndex;
	final Directory atcIndex;

	public CodeAssigner(MedDocument doc, Directory icdIndex, Directory atcIndex, QueryEngine icdQueryEngine, QueryEngine atcQueryEngine) {
		this.doc = doc;
		this.icdIndex = icdIndex;
		this.atcIndex = atcIndex;
		this.atcQueryEngine = atcQueryEngine;
		this.icdQueryEngine = icdQueryEngine;
	}

	@Override
	public void run() {
		// iterate over all sentences in MedDocument
		for (Sentence sentence : doc.getSentences()) {

			
			
			// look up ICD10 codes corresponding to this sentence and link them
			List<String> icdCodes = icdQueryEngine.lookup(sentence.getText(), icdIndex);
			sentence.addAllCodes(CodeType.ICD10, icdCodes);

			// look up ATC codes corresponding to this sentence and link them
			List<String> atcCodes = atcQueryEngine.lookup(sentence.getText(), atcIndex);
			sentence.addAllCodes(CodeType.ATC, atcCodes);
		}
	}
}
