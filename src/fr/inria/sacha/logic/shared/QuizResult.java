package fr.inria.sacha.logic.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable
public class QuizResult  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	String id;
	
	   
	@Persistent
	String authorName = "";
	 @Persistent
	Date date = new Date();
	
	 @Persistent
	 String project = "";
	 
	
	 @Persistent
	String heuristic = "";
 
	 
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String autorName) {
		this.authorName = autorName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}

	public String getHeuristic() {
		return heuristic;
	}
	public void setHeuristic(String heuristic) {
		this.heuristic = heuristic;
	}
	
	
	 @Persistent
	 @Element(dependent = "true")
	 List<QuizItemResult> resultados = new ArrayList<QuizItemResult>();
	
	 public List<QuizItemResult> getResultados() {
			return resultados;
		}
		public void setResultados(List<QuizItemResult> resultados) {
			this.resultados = resultados;
		}
		@Override
		public String toString() {
			return "QuizResult [autorName=" + authorName + ", date=" + date + ", heuristic="+heuristic
					+ ", project=" + project + ", resultados=" + resultados + "]";
		}
}
