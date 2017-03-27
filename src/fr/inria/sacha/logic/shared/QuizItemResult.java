package fr.inria.sacha.logic.shared;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
//@Entity
public class QuizItemResult  implements Serializable{


	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	String id;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	  @Persistent
	  String isBugFix;
	  @Persistent
	  String representation;
	  
	 @Persistent
	String comments;
	 
	 @Persistent
	 String gtVsDiff;

	 @Persistent
	String transactionID = "";
	 
	 @Persistent
	 boolean anomalyCD = false;
	 
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "QuizItemResult [isBugFix=" + isBugFix + ", representacion="
				+ representation + ", comments=" + comments + "]";
	}

	public String getIsBugFix() {
		return isBugFix;
	}

	public void setIsBugFix(String isBugFix) {
		this.isBugFix = isBugFix;
	}

	public String getRepresentation() {
		return representation;
	}

	public void setRepresentation(String representation) {
		this.representation = representation;
	}

	/*public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}
	*/
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getGtVsDiff() {
		return gtVsDiff;
	}

	public void setGtVsDiff(String gtVsDiff) {
		this.gtVsDiff = gtVsDiff;
	}

	public boolean isAnomalyCD() {
		return anomalyCD;
	}

	public void setAnomalyCD(boolean anomalyCD) {
		this.anomalyCD = anomalyCD;
	}
	
	
	
}
