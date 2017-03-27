package fr.inria.sacha.quizGumTree.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import fr.inria.sacha.logic.shared.BugFixQuizResult;
import fr.inria.sacha.logic.shared.QuizResult;


public class DataManager {

	
	public void save(QuizResult quiz){
		PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
        	pm.makePersistent(quiz);
        } finally {
        	 Query q = pm.newQuery(QuizResult.class);
        	 System.out.println("result "+q.execute());
        	pm.close();
        }
       System.out.println("Post-Close "+getQuiz());
	}
	
	public List<QuizResult> getQuiz(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(QuizResult.class);
		List<QuizResult> result = (List<QuizResult>) q.execute();
		System.out.println("Pre-Close "+result);
		pm.close();
		return result;
	}
	
	public List<String> getDatasetDone(String user){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<String> response = new ArrayList<String>();
		Query qproj = pm.newQuery("select project, heuristic from fr.inria.sacha.logic.shared.QuizResult group by project, heuristic");
		qproj.setFilter("authorName == '"+user+"' ");
		List<Object[]> datasets = (List<Object[]>) qproj.execute();
		for(Object[] data:datasets){
			response.add(data[0].toString()+"-"+data[1].toString());
		}
		
		System.out.println("Pre-Close "+ datasets);
		pm.close();
		return response;
	}
}
