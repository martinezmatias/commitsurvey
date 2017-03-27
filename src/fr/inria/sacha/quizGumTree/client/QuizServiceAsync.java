package fr.inria.sacha.quizGumTree.client;

import java.util.List;


import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.inria.sacha.logic.shared.QuizItem;
import fr.inria.sacha.logic.shared.QuizResult;


public interface QuizServiceAsync {
	

	
	void startQuiz(String user, String dataset, AsyncCallback<Integer> callback);

	void saveResult(QuizResult result, AsyncCallback<Boolean> callback);

	void getQuizTransaction(String user, AsyncCallback<QuizItem> callback);

	void getProjects(String user, AsyncCallback<List<String>> callback);
}
