package fr.inria.sacha.logic.shared;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class BFPatternHunkResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	String id;
	@Persistent
	String hunk;
	@Persistent
	String pattern;
	@Persistent
	String panBugFixParttern;
	@Persistent
	String discoverBy;
	
	public BFPatternHunkResult() {}
	
	public BFPatternHunkResult(String hunk, String pattern) {
		super();
		this.hunk = hunk;
		this.pattern = pattern;
	}

	public String getHunk() {
		return hunk;
	}

	public void setHunk(String hunk) {
		this.hunk = hunk;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getPanBugFixParttern() {
		return panBugFixParttern;
	}

	public void setPanBugFixParttern(String panBugFixParttern) {
		this.panBugFixParttern = panBugFixParttern;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDiscoverBy() {
		return discoverBy;
	}

	public void setDiscoverBy(String discoverBy) {
		this.discoverBy = discoverBy;
	}

}
