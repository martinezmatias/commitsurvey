package fr.inria.sacha.logic.shared;

import java.io.Serializable;

public class SummaryResult implements Serializable {

	public SummaryResult() {
	
	}
	public SummaryResult(String dataset) {
		super();
		this.dataset = dataset;
	}
	public String dataset = null;

	public int surveys = 0;

	public int completeBF = 0;
	public int partialBF = 0;
	public int incompleteBF = 0;

	public int[][] gtBetterThanDiff = new int[3][4];
	public int[][] isBugFix = new int[3][4];
	public int[][] gtGoodJob = new int[3][4];

	public int completeGTPerformance = 0;
	public int partialGTPerformance = 0;
	public int incompleteGTPerformance = 0;

	public int completeGTBest = 0;
	public int partialGTBest = 0;
	public int incompleteGTBest = 0;

}
