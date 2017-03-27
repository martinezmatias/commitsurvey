package fr.inria.sacha.quizGumTree.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import fr.inria.sacha.logic.shared.QuizItem;
import fr.inria.sacha.logic.shared.QuizItemFactory;
import fr.inria.sacha.logic.shared.QuizItemResult;
import fr.inria.sacha.logic.shared.QuizResult;
import fr.inria.sacha.quizGumTree.client.QuizService;


@SuppressWarnings("serial")
public class QuizServiceImpl extends RemoteServiceServlet implements
		QuizService {

	
	public static String DATASET_FOLDER_NAME = "datasets" ;
	
	private static final Logger log = Logger.getLogger(QuizServiceImpl.class.getName());
	
	private DataManager dm = new DataManager();
	
	public Integer startQuiz(String user, String dataset) {
		File f = new File(DATASET_FOLDER_NAME+File.separator+dataset);
		File[]  cases = f.listFiles();
		log.info("User "+user+" starts "+dataset+",  cases :"+Arrays.toString(cases));
		saveCases(user, cases);
		saveCursor(user, 0);
		return cases.length;
	}
	

	@Override
	public Boolean saveResult(QuizResult result) {
		log.info("Saving results "+result +" for "+result);
		File[] cases = getFiles(result.getAuthorName());
		putTransactionId(result,cases);
		dm.save(result);
		cleanSession(result.getAuthorName());
		return null;
	}


	private void putTransactionId(QuizResult result, File[] cases2) {
		for(int i=0;i<result.getResultados().size();i++){
			QuizItemResult item = result.getResultados().get(i);
			item.setTransactionID(getId(cases2[i]));
			
		}
		
	}


	private String getId(File file) {
		
		return file.getName().replace("t_", "");
	}


	@Override
	public QuizItem getQuizTransaction(String user) {
		log.info("Demand of new item from user "+user);
		Integer counter = getCursor(user);
		File[] cases = getFiles(user);
		
		if(counter  == cases.length){
			return null;
		}
	//	log.info("Before prepare "+(counter+1));
		QuizItemFactory itemFactory = new QuizItemFactory();
		
		QuizItem response = itemFactory.createQuizItem(cases[counter]);
	//	log.info("After Prepared "+(counter+1));
		counter++;
		saveCursor(user, counter);
		response.setIndex(counter);
		log.info("User " + user +" getting "+counter + " of "+cases.length+", id "+response.getTransactionID());
		return response;
	}
	
	

	public void cleanSession(String user){
		saveCases(user, null);
		saveCursor(user, null);
		saveDataset(user, null);
		
	}

	@Override
	public List<String> getProjects(String user) {
		List<String> projects = new ArrayList<String>();
		DataManager dm = new DataManager();
		List<String>  done = dm.getDatasetDone(user);
		File f = new File(DATASET_FOLDER_NAME);

		for(String name : f.list()){
			if(!done.contains(name) && (!name.endsWith("1LC"))){
				projects.add(name);
			}
		}
		log.info("Projects to do for user "+user +": "+ projects.toString());
		
		return projects;
	}
	
	public void saveDataset(String user, String dataset){
		getThreadLocalRequest().getSession().setAttribute(user+"-ds", dataset);
	}
	public String getDataset(String user){
		return getThreadLocalRequest().getSession().getAttribute(user+"-ds").toString();
	}
	
	public void saveCursor(String user, Integer dataset){
		getThreadLocalRequest().getSession().setAttribute(user+"-cursor", dataset);
	}
	public Integer getCursor(String user){
		return (Integer) getThreadLocalRequest().getSession().getAttribute(user+"-cursor");
	}
	
	public void saveCases(String user, File[] files){
		getThreadLocalRequest().getSession().setAttribute(user+"-files", files);
	}
	public File[] getFiles(String user){
		return (File[]) getThreadLocalRequest().getSession().getAttribute(user+"-files");
	}
	
}
