package no.ntnu.tdt4215.group7.entity;

public class EvaluationResult {

	private final String idCase;

	int found;

	int falseNegative;

	int falsePositive;

	public EvaluationResult(String idCase) {
		super();
		this.idCase = idCase;
	}

	public String getIdCase() {
		return idCase;
	}

	public int getFound() {
		return found;
	}

	public int getFalseNegative() {
		return falseNegative;
	}

	public int getFalsePositive() {
		return falsePositive;
	}

	public void addFalseNegative() {
		falseNegative++;
	}
	
	public void addFalsePositive() {
		falsePositive++;
	}
	
	public void addFound() {
		found++;
	}
}
