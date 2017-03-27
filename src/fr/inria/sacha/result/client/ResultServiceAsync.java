package fr.inria.sacha.result.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fr.inria.sacha.logic.shared.SummaryResult;

public interface ResultServiceAsync {

	void getResults(AsyncCallback<List<SummaryResult>> callback);

	
}
