package fr.inria.sacha.quizBFP.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.inria.sacha.logic.shared.BugFixPatternResult;
import fr.inria.sacha.logic.shared.BugFixQuizItem;
import fr.inria.sacha.logic.shared.BugFixQuizResult;

public interface SurveyBFPServiceAsync {

	void start(String user, String dataset, AsyncCallback<Integer> callback);

	void getNextBugFixItem(String user, AsyncCallback<BugFixQuizItem> callback);

	void saveResult(BugFixQuizResult result, AsyncCallback<Void> callback);

	void getClassification(String user, AsyncCallback<List<String>> callback);

	void analyzeResults(AsyncCallback<List<BugFixPatternResult>> callback);

}
