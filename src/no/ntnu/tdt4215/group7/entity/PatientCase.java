package no.ntnu.tdt4215.group7.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatientCase {
	
	int id;
	
	List<Sentence> sentences;

	public int getId() {
		return id;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addSentence(String text) {
		Sentence sentence = new Sentence();
		sentence.setText(text);
		
		this.sentences.add(sentence);
	}
	
	public void addSentences(List<String> sentences) {
		for(String text : sentences) {
			Sentence sentence = new Sentence();
			sentence.setText(text);
			
			this.sentences.add(sentence);
		}
	}

	public PatientCase() {
		sentences = new ArrayList<Sentence>();
	}
	
	public Set<String> getAllCodes(CodeType codeType) {
		Set<String> codeSet = new HashSet<String>();
		
		for(Sentence sentence : sentences) {
			codeSet.addAll(sentence.getCodes(codeType));
		}
		
		return codeSet;
	}
}
