package no.ntnu.tdt4215.group7.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sentence {

	String text;

	Map<CodeType, List<String>> codeMap = new HashMap<CodeType, List<String>>();

	public String getText() {
		return text;
	}

	public List<String> getCodes(CodeType codeType) {
		if(codeMap.get(codeType) == null) {
			codeMap.put(codeType, new ArrayList<String>());
		}
		return codeMap.get(codeType);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void addAllCodes(CodeType codeType, List<String> codes) {
		if (codeMap.get(codeType) == null) {
			codeMap.put(codeType, new ArrayList<String>());
		}
		this.codeMap.get(codeType).addAll(codes);
	}

	public void addCode(CodeType codeType, String code) {
		if (codeMap.get(codeType) == null) {
			codeMap.put(codeType, new ArrayList<String>());
		}
		this.codeMap.get(codeType).add(code);
	}

	public boolean containsCode(CodeType codeType, String code) {
		if (codeMap.get(codeType) == null) {
			return false;
		}

		return codeMap.get(codeType).contains(code); //FIXME
	}

	public boolean match(Sentence input) {
		// check all the code lists in the code map
		for (CodeType codeType : CodeType.values()) {
			// some codes don't have code lists
			if (codeType.isSkipped()) {
				continue;
			}

			// return true, if input sentence contains any of the codes from this sentence
			for (String code : this.getCodes(codeType)) {
				return input.containsCode(codeType, code);
			}
		}

		return false;
	}

}
