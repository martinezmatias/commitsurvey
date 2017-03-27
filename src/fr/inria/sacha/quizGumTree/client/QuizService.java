package fr.inria.sacha.quizGumTree.client;

import java.util.List;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.inria.sacha.logic.shared.QuizItem;
import fr.inria.sacha.logic.shared.QuizResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("survey")
public interface QuizService extends RemoteService {
	

	Integer startQuiz(String user,String dataset);
	
	Boolean saveResult(QuizResult result);
		
	QuizItem getQuizTransaction(String user);
	
	List<String> getProjects(String user);
	
}
