package no.ntnu.tdt4215.group7.entity;

import java.util.ArrayList;
import java.util.List;

public class PatientCase {
	
	int id;
	
	List<String> sentences;

	public int getId() {
		return id;
	}

	public List<String> getSentences() {
		return sentences;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addSentence(String sentence) {
		this.sentences.add(sentence);
	}
	
	public void addSentence(List<String> sentences) {
		this.sentences.addAll(sentences);
	}

	public PatientCase() {
		sentences = new ArrayList<String>();
	}
}
