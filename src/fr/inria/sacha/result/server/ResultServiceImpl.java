package fr.inria.sacha.result.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import fr.inria.sacha.logic.shared.QuizItemResult;
import fr.inria.sacha.logic.shared.QuizResult;
import fr.inria.sacha.logic.shared.ResultType;
import fr.inria.sacha.logic.shared.SummaryResult;
import fr.inria.sacha.quizGumTree.server.PMF;
import fr.inria.sacha.quizGumTree.server.QuizServiceImpl;
import fr.inria.sacha.result.client.ResultService;

public class ResultServiceImpl extends RemoteServiceServlet implements ResultService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PersistenceManager pm = PMF.get().getPersistenceManager();

	private static final Logger log = Logger.getLogger(QuizServiceImpl.class.getName());

	List<Counters> sujects = new ArrayList<Counters>();
	
	public List<SummaryResult> getResults() {
		sujects.clear();
		List<SummaryResult> response = new ArrayList<SummaryResult>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(QuizResult.class);
			
		SummaryResult summaryAll = new SummaryResult("All-Datasets");
		Query qproj = pm
				.newQuery("select project, heuristic from fr.inria.sacha.logic.shared.QuizResult group by project, heuristic");
		List<String[]> datasets = (List<String[]>) qproj.execute();
		
		for (Object[] dataset : datasets) {
			q = pm.newQuery(QuizResult.class);
			q.setFilter("project == '" + dataset[0] + "'"
					+ ((!"".equals(dataset[1])) ? (" && heuristic == '" + dataset[1] + "'") : "  && heuristic == ''"));
			
			List<QuizResult> 	quizResultOfDataset = (List<QuizResult>) q.execute();
			if (quizResultOfDataset.size() == 0)
				continue;
		
			String datasetName = dataset[0] + "-" + dataset[1];
			log.info("Analyzing dataset "+datasetName);
			//resultbymail(quizResultOfDataset, datasetName);
			SummaryResult summary = null;	
			try {
				summary = processQuizResult(quizResultOfDataset, datasetName);
				addDatasetResultToAll(summaryAll, summary);
			} catch (Exception e) {
				e.printStackTrace();
				pm.close();
				return null;
			}
			
			response.add(summary);
		}
		pm.close();
		response.add(0,summaryAll);
		log.info("End results");
		analyzeResults(sujects);
		return response;
	}
	
	private void analyzeResults(List<Counters> sujects2) {
		String bf = "";
		String GTvsDiff = "";
		String GTPerformance = "";
		for (Counters counters : sujects2) {
			//bf+=(counters.bfq_bf+"\t"+counters.bfq_nbf+"\t"+counters.bfq_idk+"\n");
			bf+=(counters.transaction +"\t" + counters.bfresponse[0]+"\t"+counters.bfresponse[1]+"\t"+counters.bfresponse[2]+"\n");
			
			//GTvsDiff+=(counters.compq_GTbest+"\t"+counters.compq_DiffBest+"\t"+counters.compq_idk+"\n");
			GTvsDiff+=(counters.transaction +"\t" +counters.comparative_response[0]+"\t"+counters.comparative_response[1]+"\t"+counters.comparative_response[2]+"\n");
			
		//	GTPerformance+= (counters.per_good+"\t"+counters.per_notgood+"\t"+counters.per_neutral+"\n");
			GTPerformance+= (counters.transaction +"\t" +counters.performance_sponse[0]+"\t"+counters.performance_sponse[1]+"\t"+counters.performance_sponse[2]+"\n");
			
			
			
		}
		sendMail("bf", bf);
		sendMail("GTvsDiff", GTvsDiff);
		sendMail("GTPerf", GTPerformance);
		
	}

	public void resultbymail(List<QuizResult> 	quizResultOfDataset,String datasetName){
		
		  String msgBody = "";
		for (QuizResult quizResult : quizResultOfDataset) {
			for (QuizItemResult quizItemResult : quizResult.getResultados()) {
				msgBody+= quizResult.getAuthorName()+","+quizItemResult.getTransactionID()+","+quizItemResult.getIsBugFix()+","+quizItemResult.getGtVsDiff()+","+quizItemResult.getRepresentation()+","+quizItemResult.getComments()+"\n";
			
			}
			
		}
		sendMail(datasetName, msgBody);
		
		
		
	}

	public void sendMail(String datasetName, String msgBody) {
		
		 Properties props = new Properties();
	        Session session = Session.getDefaultInstance(props, null);

	        try {
	            Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress("matoaldosivi@gmail.com", "Spirals"));
	            msg.addRecipient(Message.RecipientType.TO,
	                             new InternetAddress("matomartinez@gmail.com", "Matias"));
	            msg.setSubject(datasetName);
	            msg.setText(msgBody);
	            Transport.send(msg);

	        } catch (AddressException e) {
	            // ...
	        } catch (MessagingException e) {
	            // ...
	        } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void addDatasetResultToAll(SummaryResult summaryAll, SummaryResult partial){
		
		summaryAll.surveys += partial.surveys;

		summaryAll.completeBF += partial.completeBF;
		summaryAll.partialBF += partial.partialBF;
		summaryAll.incompleteBF += partial.incompleteBF;

		summaryAll.completeGTPerformance += partial.completeGTPerformance;
		summaryAll.partialGTPerformance += partial.partialGTPerformance;
		summaryAll.incompleteGTPerformance += partial.incompleteGTPerformance;

		summaryAll.completeGTBest += partial.completeGTBest;
		summaryAll.partialGTBest += partial.partialGTBest;
		summaryAll.incompleteGTBest += partial.incompleteGTBest;
		int x = summaryAll.isBugFix.length;
		int y = summaryAll.isBugFix[0].length;
		for(int i = 0;i<x;i++){
			for(int j=0;j<y;j++){
				summaryAll.isBugFix[i][j]+= partial.isBugFix[i][j];
				summaryAll.gtGoodJob[i][j]+= partial.gtGoodJob[i][j];
				summaryAll.gtBetterThanDiff[i][j]+= partial.gtBetterThanDiff[i][j];
			}
		}
	}
	
	
	public void completeSummary(List<Integer> items, int users, int[][] results) {
		for (int i = 0; i < items.size(); i++) {
			double percentageAgreement = ((double) items.get(i)) / ((double) users);
			if (percentageAgreement > 0) {
				int agreeLevel = levelAgreement(percentageAgreement);
				results[i][agreeLevel]++;
			}
		}
	}



	private SummaryResult processQuizResult(List<QuizResult> result, String datasetname) throws Exception {
		SummaryResult summary = new SummaryResult();
		summary.dataset = datasetname;
		int numberItems = result.get(0).getResultados().size();
		summary.surveys = result.size();
		return processQuizResult(summary,numberItems,result,datasetname);
		
	}
	
	private SummaryResult processQuizResult(SummaryResult summary,int numberItems,List<QuizResult> result, String datasetname) throws Exception {
			
		 Map<String, QuizItemResult[]>  itemsByTransactions = organizeItemsPerTransaction(result);
		int nroTransactions = itemsByTransactions.keySet().size();
		//for (int i = 0; i < numberItems; i++) {
		 for(String transaction : itemsByTransactions.keySet()){
			//Counters save partial  results of Item 
			Counters counter = new Counters(transaction);			
			int users = result.size();
			sujects.add(counter);
			String lastT = null;
			for (int user = 0; user < users; user++) {
							
				//QuizItemResult itemOfUser = result.get(user).getResultados().get(i);
				QuizItemResult itemOfUser = itemsByTransactions.get(transaction)[user];
								
				//log.info("--Analyzing user " + user+"---item "+itemOfUser.getTransactionID());
				
				//--Validation transaction id
				if(lastT== null ||  lastT.equals(itemOfUser.getTransactionID())){
					lastT = itemOfUser.getTransactionID();
				}
				else{
					log.severe("Item Transaction Are Unorder");
					throw new Exception("Item Transaction Are Unorder");
				}
				//--
				analyzeBugFixItemForItem(itemOfUser, summary, counter);
				counter.bfresponse[user] = itemOfUser.getIsBugFix(); 
				//--
				analyzeGTDiffCompQuizForItem(itemOfUser, summary, counter);
				counter.comparative_response[user] = itemOfUser.getGtVsDiff();
				
				//--
				analyzeGTPerformanceQuizForItem(itemOfUser, summary, counter);
				counter.performance_sponse[user] = itemOfUser.getRepresentation();
				
				
			}//End for User
			
			//Saving result of one ITEM
			summary.completeBF = nroTransactions - summary.partialBF;
			if (!counter.partialbfq) {
				completeSummary(counter.resutlbfq(), users, summary.isBugFix);
			}
			
			summary.completeGTBest= nroTransactions - summary.partialGTBest;
			if (!counter.partialcompbfq) {
				completeSummary(counter.resultcomp(), users, summary.gtBetterThanDiff);
			}
			
			summary.completeGTPerformance= nroTransactions - summary.partialGTPerformance;
			if (!counter.partialGTPerformanceq) {
				completeSummary(counter.resultGTperformance(), users, summary.gtGoodJob);
			}
			
			
		}//End-For Item
		return summary;

	}
	public Map<String, QuizItemResult[]> organizeItemsPerTransaction(List<QuizResult> result){
		int users = result.size();
		Map<String,QuizItemResult[]> itemsByTransaction = new HashMap<String,QuizItemResult[]>();
		for (int user = 0; user < users; user++) {
			List<QuizItemResult> itemsOfUser = result.get(user).getResultados();
			int userIndex = getUser(result.get(user).getAuthorName());
			for (QuizItemResult quizItemResult : itemsOfUser) {
				String transaction = quizItemResult.getTransactionID();
				QuizItemResult[] itemsOfTrans= itemsByTransaction.get(transaction);
				if(itemsOfTrans == null){
					itemsOfTrans = new QuizItemResult[users];
					itemsByTransaction.put(transaction, itemsOfTrans);
				}
				//itemsOfTrans[user] = quizItemResult;
				itemsOfTrans[userIndex] = quizItemResult;
				}
			}
		return itemsByTransaction;
	}

	public void analyzeGTPerformanceQuizForItem(QuizItemResult itemOfUser, SummaryResult summary, Counters counter) {
		if (itemOfUser.getRepresentation() == null || "".equals(itemOfUser.getRepresentation().trim())) {
			summary.partialGTPerformance++;
			counter.partialGTPerformanceq= true;
		} else
			switch (ResultType.valueOf(itemOfUser.getRepresentation().trim())) {

			case GTGOODJOB:
				counter.per_good++;
				break;
			case NEUTRAL:
				counter.per_neutral++;
				break;
			case GTNOTGOODJOB:
				counter.per_notgood++;
				break;
			default:
				break;
			}
	}

	public void analyzeGTDiffCompQuizForItem(QuizItemResult itemOfUser, SummaryResult summary, Counters counter) {
		if (itemOfUser.getIsBugFix() == null || "".equals(itemOfUser.getIsBugFix().trim())) {
			summary.partialGTBest++;
			counter.partialcompbfq = true;
		} else
			switch (ResultType.valueOf(itemOfUser.getGtVsDiff().trim())) {

			case GUMTREE:
				counter.compq_GTbest++;
				break;
			case DONTKNOW:
				counter.compq_idk++;
				break;
			case DIFF:
				counter.compq_DiffBest++;
				break;
			default:
				break;
			}
	}

	public void analyzeBugFixItemForItem(QuizItemResult itemOfUser, SummaryResult summary, Counters counter) {
		if (itemOfUser.getIsBugFix() == null || "".equals(itemOfUser.getIsBugFix().trim())) {
			summary.partialBF++;
			counter.partialbfq = true;
		} else
			switch (ResultType.valueOf(itemOfUser.getIsBugFix().trim())) {

			case BUGFIX:
				counter.bfq_bf++;
				break;
			case DONTKNOW:
				counter.bfq_idk++;
				break;
			case NOBUGFIX:
				counter.bfq_nbf++;
				break;
			default:
				break;
			}
		
	}

	public class Counters{
		
		String transaction = "";
		
		public Counters(String transaction) {
			super();
			this.transaction = transaction;
		}

		int bfq_bf = 0;
		int bfq_nbf = 0;
		int bfq_idk = 0;
		boolean partialbfq = false;
		
		
		
		public List resutlbfq(){
		List items = new ArrayList();
		items.add(bfq_bf);
		items.add(bfq_nbf);
		items.add(bfq_idk);
		return items;
		}
		
		////
		int compq_GTbest = 0;
		int compq_DiffBest = 0;
		int compq_idk = 0;
		boolean partialcompbfq = false;
		
		public List resultcomp(){
			List items = new ArrayList();
			items.add(compq_GTbest);
			items.add(compq_DiffBest);
			items.add(compq_idk);
			return items;
		}
		
		
		int per_good = 0;
		int per_notgood = 0;
		int per_neutral = 0;
		
		boolean partialGTPerformanceq = false;
		
		public List resultGTperformance(){
			List items = new ArrayList();
			items.add(per_good);
			items.add(per_notgood);
			items.add(per_neutral);
			return items;
		}
		
		public String[] bfresponse = new String[3];
		public String[] performance_sponse = new String[3];
		public String[] comparative_response = new String[3];
	}
	public int getUser(String user){
		if(user.equals("Jean-Remy"))
			return 0;
		if(user.equals("Martin"))
			return 1;
		if(user.equals("Matias"))
			return 2;
		return -1;
	} 
	
	
	public int levelAgreement(double agree) {
		if (agree == 1)
			return 0;
		if (agree > 0.60)
			return 1;
		if (agree >= 0.50)
			return 2;
		if (agree > 0)
			return 3;
		return -1;

	}
}
