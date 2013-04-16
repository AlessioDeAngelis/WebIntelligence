package no.ntnu.tdt4215.group7.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MedDocument {
	
	CodeType type;
	
	String id;
	
	List<Sentence> sentences;

	public String getId() {
		return id;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setId(String id) {
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

	public MedDocument(CodeType type) {
		this.type = type;
		sentences = new ArrayList<Sentence>();
	}
	
	public Set<String> getAllCodes(CodeType codeType) {
		Set<String> codeSet = new HashSet<String>();
		
		for(Sentence sentence : sentences) {
			codeSet.addAll(sentence.getCodes(codeType));
		}
		
		return codeSet;
	}
	
	public List<String> getTextByCode(CodeType codeType, String code) {
		List<String> results = new ArrayList<String>();
		
		for(Sentence sentence : sentences) {
			if(sentence.containsCode(codeType, code)) {
				results.add(sentence.getText());
			}
		}
		
		return results;
	}
	
	public CodeType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("doc id: " + id + " type: " + type);
		
		for(Sentence sentence : getSentences()) {
			sb.append(sentence);
		}
		
		return sb.toString();
	}
}
