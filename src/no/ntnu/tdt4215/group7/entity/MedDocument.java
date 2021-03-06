package no.ntnu.tdt4215.group7.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MedDocument {

	private CodeType type;

	private String id;

	private List<Sentence> sentences;
	
	private Set<String> relevantIds;

	public String getId() {
		return id;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Only added if not only whitespace characters
	 * @param text
	 */
	public void addSentence(String text) {
		if (!text.matches("^[\\s]*$")) {
			Sentence sentence = new Sentence();
			sentence.setText(text);

			this.sentences.add(sentence);
		}
	}
	
	public void addRelevantDocId(String text) {
		if (!text.matches("^[\\s]*$")) {
			this.relevantIds.add(text);
		}
	}

	public void addSentences(List<String> sentences) {
		for (String text : sentences) {
			Sentence sentence = new Sentence();
			sentence.setText(text);

			this.sentences.add(sentence);
		}
	}

	public MedDocument(CodeType type) {
		this.type = type;
		sentences = new ArrayList<Sentence>();
		relevantIds = new HashSet<String>();
	}

	public Set<String> getAllCodes(CodeType codeType) {
		Set<String> codeSet = new HashSet<String>();

		for (Sentence sentence : sentences) {
			codeSet.addAll(sentence.getCodes(codeType));
		}

		return codeSet;
	}

	public List<String> getTextByCode(CodeType codeType, String code) {
		List<String> results = new ArrayList<String>();

		for (Sentence sentence : sentences) {
			if (sentence.containsCode(codeType, code)) {
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
		sb.append("<doc id=\"" + id + "\" type=\"" + type + "\">");

		for (Sentence sentence : getSentences()) {
			sb.append(sentence);
		}

		sb.append("</doc>");

		return sb.toString();
	}
	
	public Set<String> getRelevantIds() {
		return relevantIds;
	}
	
	public boolean containsRelevantId(String id) {
		return relevantIds.contains(id);
	}
}
