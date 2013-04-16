package no.ntnu.tdt4215.group7.entity;

public enum CodeType {
	ICD10, ATC, LMHB, CLINICAL_NOTE;
	
	public boolean isSkipped() {
		return (equals(CLINICAL_NOTE) || equals(LMHB));
	}
}