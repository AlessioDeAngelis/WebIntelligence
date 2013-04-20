package no.ntnu.tdt4215.group7.entity;

public enum CodeType {
	ICD10, ATC, LMHB, CLINICAL_NOTE, GOLD_STANDARD;
	
	public boolean isDocumentCode() {
		return (equals(CLINICAL_NOTE) || equals(LMHB) || equals(GOLD_STANDARD));
	}
}
