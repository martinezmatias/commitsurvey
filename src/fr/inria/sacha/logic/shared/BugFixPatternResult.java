package fr.inria.sacha.logic.shared;

import java.io.Serializable;

public class BugFixPatternResult implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String dataset = "";
	
	public int[][] summary = null;
	
	public BugFixPatternResult(){
		
	}
	
	public BugFixPatternResult(String dataset, int[][] summary){
		super();
		this.dataset = dataset;
		this.summary = summary;
	}
	
	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public int[][] getSummary() {
		return summary;
	}

	public void setSummary(int[][] summary) {
		this.summary = summary;
	}


}
