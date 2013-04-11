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

	public void addAtcCode(CodeType codeType, String code) {
		if (codeMap.get(codeType) == null) {
			codeMap.put(codeType, new ArrayList<String>());
		}
		this.codeMap.get(codeType).add(code);
	}

	public boolean containsCode(CodeType codeType, String code) {
		if (codeMap.get(codeType) == null) {
			return false;
		}
		
		return codeMap.get(codeType).contains(code);
	}

}
