package fr.inria.sacha.logic.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class BugFixItemResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	String id;
	
	 @Persistent
	String transactionID = "";
	
	 @Persistent
	 @Element(dependent = "true")
	 List<BFPatternHunkResult> astEvaluation = new ArrayList<BFPatternHunkResult>();
	 @Persistent
	 @Element(dependent = "true")
	 List<BFPatternHunkResult> tokenEvaluation = new ArrayList<BFPatternHunkResult>();
	 @Persistent
	 @Element(dependent = "true")
	 List<BFPatternHunkResult> commonEvaluation = new ArrayList<BFPatternHunkResult>();
	 @Persistent
	 @Element(dependent = "true")
	 List<BFPatternHunkResult> tokenASTEvaluation = new ArrayList<BFPatternHunkResult>();
	
	 
	 @Persistent
	String comments;
		 
	 
	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public List<BFPatternHunkResult> getTokenEvaluation() {
		return tokenEvaluation;
	}

	public void setTokenEvaluation(List<BFPatternHunkResult> hunkEvaluation) {
		this.tokenEvaluation = hunkEvaluation;
	}

	public List<BFPatternHunkResult> getAstEvaluation() {
		return astEvaluation;
	}

	public void setAstEvaluation(List<BFPatternHunkResult> astEvaluation) {
		this.astEvaluation = astEvaluation;
	}

	public List<BFPatternHunkResult> getCommonEvaluation() {
		return commonEvaluation;
	}

	public void setCommonEvaluation(List<BFPatternHunkResult> commonEvaluation) {
		this.commonEvaluation = commonEvaluation;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<BFPatternHunkResult> getTokenASTEvaluation() {
		return tokenASTEvaluation;
	}

	public void setTokenASTEvaluation(List<BFPatternHunkResult> tokenASTEvaluation) {
		this.tokenASTEvaluation = tokenASTEvaluation;
	}
	
	public List<BFPatternHunkResult> getAllEvaluations(){
		 List<BFPatternHunkResult> all = new ArrayList<BFPatternHunkResult>();
			all.addAll(this.astEvaluation);
			all.addAll(this.commonEvaluation);
			all.addAll(this.tokenASTEvaluation);
			all.addAll(this.tokenEvaluation);
	
	return all; 
	}

	
}
