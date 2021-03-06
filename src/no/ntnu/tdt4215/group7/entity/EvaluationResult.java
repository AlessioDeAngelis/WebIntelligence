package no.ntnu.tdt4215.group7.entity;

public class EvaluationResult {

	private final String idCase;

	private final int truePositive;

	private final int falseNegative;

	private final int falsePositive;

	/**
	 * 
	 * @param idCase id of document (patient case)
	 * @param truePositive false positive findings
	 * @param falseNegative false positive findings
	 * @param falsePositive false negative findings
	 */
	public EvaluationResult(String idCase, int truePositive, int falseNegative, int falsePositive) {
		super();
		this.idCase = idCase;
		this.truePositive = truePositive;
		this.falseNegative = falseNegative;
		this.falsePositive = falsePositive;
	}

	public String getIdCase() {
		return idCase;
	}

	public int getTruePositive() {
		return truePositive;
	}

	public int getFalseNegative() {
		return falseNegative;
	}

	public int getFalsePositive() {
		return falsePositive;
	}
}
