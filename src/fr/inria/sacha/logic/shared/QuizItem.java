package fr.inria.sacha.logic.shared;

import java.io.Serializable;

public class QuizItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String commitMessage;
	private String leftSourceCode;
	private String rightSourceCode;
	private String dates;
	private String sccInformation;
	private String gumtreeDiff;
	private String changeDistillerDiff;
	private String astContent;
	private String transactionID;
	int index = 0;
	
	
	public String getCommitMessage() {
		return commitMessage;
	}
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
	public String getLeftSourceCode() {
		return leftSourceCode;
	}
	public void setLeftSourceCode(String leftSourceCode) {
		this.leftSourceCode = leftSourceCode;
	}
	public String getRightSourceCode() {
		return rightSourceCode;
	}
	public void setRightSourceCode(String rightSourceCode) {
		this.rightSourceCode = rightSourceCode;
	}
	public String getDates() {
		return dates;
	}
	public void setDates(String dates) {
		this.dates = dates;
	}
	public String getSccInformation() {
		return sccInformation;
	}
	public void setSccInformation(String sccInformation) {
		this.sccInformation = sccInformation;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getGumtreeDiff() {
		return gumtreeDiff;
	}
	public void setGumtreeDiff(String gumtreeDiff) {
		this.gumtreeDiff = gumtreeDiff;
	}
	public String getChangeDistillerDiff() {
		return changeDistillerDiff;
	}
	public void setChangeDistillerDiff(String changeDistillerDiff) {
		this.changeDistillerDiff = changeDistillerDiff;
	}
	public String getAstContent() {
		return astContent;
	}
	public void setAstContent(String astContent) {
		this.astContent = astContent;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	
	
	
}
