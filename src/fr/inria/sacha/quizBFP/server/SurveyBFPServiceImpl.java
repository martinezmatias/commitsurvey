/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package fr.inria.sacha.quizBFP.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.inria.sasha.utils.QueryWarehouse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import fr.inria.sacha.logic.shared.BFPatternHunkResult;
import fr.inria.sacha.logic.shared.BugFixItemResult;
import fr.inria.sacha.logic.shared.BugFixPatternResult;
import fr.inria.sacha.logic.shared.BugFixQuizItem;
import fr.inria.sacha.logic.shared.BugFixQuizResult;
import fr.inria.sacha.logic.shared.QuizItemFactory;
import fr.inria.sacha.logic.shared.ResultType;
import fr.inria.sacha.quizBFP.client.SurveyBFPService;
import fr.inria.sacha.quizGumTree.server.DataManager;

public class SurveyBFPServiceImpl extends RemoteServiceServlet implements SurveyBFPService {

	public static String DATASET_FOLDER_NAME = "patternResult";

	private static final Logger log = Logger.getLogger(SurveyBFPServiceImpl.class.getName());

	public static int MAX_NUMBER_ITEM_PER_SURVEY = 205;
	
	@Override
	public Integer start(String user, String dataset) {
		File f = new File(DATASET_FOLDER_NAME + File.separator + dataset);
		File[] cases = f.listFiles();
		log.info("User " + user + " starts " + dataset + ",  cases :" + Arrays.toString(cases));
		saveCases(user, cases);
		saveCursor(user, 0);
		
		return (cases != null) ? cases.length : 0;
	}

	public BugFixQuizItem getNextBugFixItem(String user) {
		log.info("Demand of new item from user " + user);
		Integer counter = getCursor(user);
		File[] cases = getFiles(user);

		if (counter == cases.length || counter >= MAX_NUMBER_ITEM_PER_SURVEY ) {
			return null;
		}
		
		QuizItemFactory itemFactory = new QuizItemFactory();

		BugFixQuizItem response = itemFactory.createBugFixItem(cases[counter]);
		counter++;
		saveCursor(user, counter);
		response.setIndex(counter);
		log.info("User " + user + " getting " + counter + " of " + cases.length + ", id " + response.getTransactionID());
	
		return response;
	}

	public void saveCursor(String user, Integer dataset) {
		getThreadLocalRequest().getSession().setAttribute(user + "-cursor", dataset);
	}

	public Integer getCursor(String user) {
		return (Integer) getThreadLocalRequest().getSession().getAttribute(user + "-cursor");
	}

	public void saveCases(String user, File[] files) {
		getThreadLocalRequest().getSession().setAttribute(user + "-files", files);
	}

	public File[] getFiles(String user) {
		return (File[]) getThreadLocalRequest().getSession().getAttribute(user + "-files");
	}

	@Override
	public void saveResult(BugFixQuizResult result) {
		log.info("Saving" + result);
		save(result);

	}

	public void save(BugFixQuizResult quiz) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			pm.makePersistent(quiz);
		} finally {
		//	Query q = pm.newQuery(BugFixQuizResult.class);
		//	System.out.println("result bfp " + q.execute());
			pm.close();
		}

	}

	@Override
	public List<String> getClassification(String user) {
		List<String> projects = new ArrayList<String>();
		DataManager dm = new DataManager();
		List<String> done = dm.getDatasetDone(user);
		File f = new File(DATASET_FOLDER_NAME);

		for (String name : f.list()) {
			projects.add(name);

		}
		log.info("Projects to do for user " + user + ": " + projects.toString());

		return projects;
	}

	public List<BugFixPatternResult> analyzeResults() {
		List<BugFixPatternResult> result = new ArrayList<BugFixPatternResult>();
	
			
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query q = pm.newQuery(BugFixQuizResult.class);
		Object r = q.execute();
		List<BugFixQuizResult> rs = (List<BugFixQuizResult>) r;
		//System.out.println("result bfp " + rs);
		int[][] summary = getSummaryResult(rs);
		
		result.add(new BugFixPatternResult("All", summary));
		
		//---
		Query qproj = pm
				.newQuery("select project, heuristic from fr.inria.sacha.logic.shared.BugFixQuizResult group by project, heuristic");
		List<String[]> datasets = (List<String[]>) qproj.execute();
		
		for (Object[] dataset : datasets) {
			q = pm.newQuery(BugFixQuizResult.class);
			q.setFilter("project == '" + dataset[0] + "'"
					+ ((!"".equals(dataset[1])) ? (" && heuristic == '" + dataset[1] + "'") : "  && heuristic == ''"));
			
			List<BugFixQuizResult> 	quizResultOfDataset = (List<BugFixQuizResult>) q.execute();
			System.out.println("Result for "+dataset[0]+ " "+quizResultOfDataset +" total: "+quizResultOfDataset.size() );
			if (quizResultOfDataset.size() == 0)
				continue;
			
			int[][] summaryProject = getSummaryResult(quizResultOfDataset);
			result.add(new BugFixPatternResult(dataset[0].toString(), summaryProject));
			
		}
		//----
		pm.close();
		//--
		exportResults();
		//---
		return result;
	}

	public int[][] getSummaryResult(List<BugFixQuizResult> rs) {
		int[][] summary = new int[3][3];
		for (BugFixQuizResult bugFixQuizResult : rs) {
		
			System.out.println("Analyze made for " + bugFixQuizResult.getAuthorName() + " "
					+ bugFixQuizResult.getProject());
			System.out.println("items "+bugFixQuizResult.getResultados().size());
			
			for (BugFixItemResult item : bugFixQuizResult.getResultados()) {
				System.out.println("---Analyzing revision " + item.getTransactionID());
					System.out.println("----");
				// AST
				System.out.println("ast "+item.getAstEvaluation().size()); 
				int[] resHunk = analyzeHunkResult(item.getAstEvaluation());
				summary[0][0] += resHunk[0];
				summary[0][1] += resHunk[1];
				//putting missing of Token
				summary[1][2] += resHunk[1];
				
				// Token
				System.out.println("token ast "+item.getTokenASTEvaluation().size()); 
				resHunk = analyzeHunkResult(item.getTokenASTEvaluation());
				summary[1][0] += resHunk[0];
				summary[1][1] += resHunk[1];
				//putting missing of 		
				summary[0][2] += resHunk[1];
				
				System.out.println("Common "+item.getCommonEvaluation().size()); 
				
				resHunk = analyzeHunkResult(item.getCommonEvaluation());
				summary[0][0] += resHunk[0];
				summary[0][1] += resHunk[1];
				summary[1][0] += resHunk[0];
				summary[1][1] += resHunk[1];
				
				System.out.println("Token "+item.getTokenEvaluation().size()); 
								
				resHunk = analyzeHunkResult(item.getTokenEvaluation());
				summary[2][0] += resHunk[0];
				summary[2][1] += resHunk[1];
				
			}

		}
		return summary;
	}

	
	private int[] analyzeHunkResult(List<BFPatternHunkResult> res) {
		int[] result = new int[2];
		for (BFPatternHunkResult hunkres : res) {
			if (hunkres.getPanBugFixParttern().equals(ResultType.BUGFIXPATTERN.toString())) {
				//System.out.println("Is pattern");
				result[1]++;
			} else {
				//System.out.println("Is not pattern");
				result[0]++;
			}
		}
		return result;
	}
	
	public int[][] getSummaryResultOLD(List<BugFixQuizResult> rs) {
		int[][] summary = new int[3][2];
		for (BugFixQuizResult bugFixQuizResult : rs) {
		/*	System.out.println("Analyze " + bugFixQuizResult + " made for " + bugFixQuizResult.getAuthorName() + " "
					+ bugFixQuizResult.getProject());
		*/	
			System.out.println("Analyze made for " + bugFixQuizResult.getAuthorName() + " "
					+ bugFixQuizResult.getProject());
			System.out.println("items "+bugFixQuizResult.getResultados().size());
			
			for (BugFixItemResult item : bugFixQuizResult.getResultados()) {
				System.out.println("---Analyzing revision " + item.getTransactionID());
				System.out.println("----");
				// AST
				System.out.println("ast "+item.getAstEvaluation().size()); 
				int[] resHunk = analyzeHunkResult(item.getAstEvaluation());
				summary[0][0] += resHunk[0];
				summary[0][1] += resHunk[1];
				// Token
				System.out.println("token ast "+item.getTokenASTEvaluation().size()); 
				resHunk = analyzeHunkResult(item.getTokenASTEvaluation());
				summary[1][0] += resHunk[0];
				summary[1][1] += resHunk[1];
						
				System.out.println("Common "+item.getCommonEvaluation().size()); 
				resHunk = analyzeHunkResult(item.getCommonEvaluation());
				summary[0][0] += resHunk[0];
				summary[0][1] += resHunk[1];
				summary[1][0] += resHunk[0];
				summary[1][1] += resHunk[1];
				
				System.out.println("Token "+item.getTokenEvaluation().size()); 
				resHunk = analyzeHunkResult(item.getTokenEvaluation());
				summary[2][0] += resHunk[0];
				summary[2][1] += resHunk[1];
				
			}

		}
		return summary;
	}
	private int[] analyzeHunkResultOLD(List<BFPatternHunkResult> res) {
		int[] result = new int[2];
		for (BFPatternHunkResult hunkres : res) {
			if (hunkres.getPanBugFixParttern().equals(ResultType.BUGFIXPATTERN.toString())) {
				//System.out.println("Is pattern");
				result[1]++;
			} else {
				//System.out.println("Is not pattern");
				result[0]++;
			}
		}
		return result;
	}
	static List<String> datasetToAnalyze = new ArrayList<String>();
	static{
		datasetToAnalyze.add("all-cases");
		datasetToAnalyze.add("none");
		datasetToAnalyze.add("not-common");
		datasetToAnalyze.add("only-ast");
		datasetToAnalyze.add("only-common");
		datasetToAnalyze.add("only-token");
		datasetToAnalyze.add("only-token-AST");
		datasetToAnalyze.add("only-token-notAST");
		datasetToAnalyze.add("token-common");
	}
	public List<BugFixPatternResult> exportResults() {
		List<BugFixPatternResult> result = new ArrayList<BugFixPatternResult>();
		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query q = null;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("survey");
		doc.appendChild(rootElement);
 
		//---
		Query qproj = pm
				.newQuery("select project, heuristic from fr.inria.sacha.logic.shared.BugFixQuizResult group by project, heuristic");
		List<String[]> datasets = (List<String[]>) qproj.execute();
		
		for (Object[] dataset : datasets) {
			if(datasetToAnalyze.contains(dataset[0].toString()))
				continue;
			
			q = pm.newQuery(BugFixQuizResult.class);
			q.setFilter("project == '" + dataset[0] + "'"
					+ ((!"".equals(dataset[1])) ? (" && heuristic == '" + dataset[1] + "'") : "  && heuristic == ''"));
			
			List<BugFixQuizResult> 	quizResultOfDataset = (List<BugFixQuizResult>) q.execute();
			System.out.println("Result for "+dataset[0]+ " "+quizResultOfDataset +" total: "+quizResultOfDataset.size() );
			if (quizResultOfDataset.size() == 0)
				continue;
			Element cat = doc.createElement("pattern");
			rootElement.appendChild(cat);
			cat.setAttribute("name", dataset[0].toString());
			int[][] summaryProject = exportResult2(dataset[0].toString(),quizResultOfDataset,cat/*rootElement*/,doc);
			result.add(new BugFixPatternResult(dataset[0].toString(), summaryProject));
			
		}
		//----
		pm.close();
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult resultxml = new StreamResult(System.out);
		 
				transformer.transform(source, resultxml);
	}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				//--
		return result;
	}
	
	public List<BugFixPatternResult> exportResults2() {
		List<BugFixPatternResult> result = new ArrayList<BugFixPatternResult>();
		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query q = null;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("survey");
		doc.appendChild(rootElement);
 
		//---
		Query qproj = pm
				.newQuery("select project, heuristic from fr.inria.sacha.logic.shared.BugFixQuizResult group by project, heuristic");
		List<String[]> datasets = (List<String[]>) qproj.execute();
		
		for (Object[] dataset : datasets) {
			if(!datasetToAnalyze.contains(dataset[0].toString()))
				continue;
			
			q = pm.newQuery(BugFixQuizResult.class);
			q.setFilter("project == '" + dataset[0] + "'"
					+ ((!"".equals(dataset[1])) ? (" && heuristic == '" + dataset[1] + "'") : "  && heuristic == ''"));
			
			List<BugFixQuizResult> 	quizResultOfDataset = (List<BugFixQuizResult>) q.execute();
			System.out.println("Result for "+dataset[0]+ " "+quizResultOfDataset +" total: "+quizResultOfDataset.size() );
			if (quizResultOfDataset.size() == 0)
				continue;
			
			int[][] summaryProject = exportResult(quizResultOfDataset,rootElement,doc);
			result.add(new BugFixPatternResult(dataset[0].toString(), summaryProject));
			
		}
		//----
		pm.close();
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult resultxml = new StreamResult(System.out);
		 
				transformer.transform(source, resultxml);
	}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				//--
		return result;
	}
	
	
	public int[][] exportResult(List<BugFixQuizResult> rs, Element rootElement, Document doc) {
		int[][] summary = new int[3][3];
		for (BugFixQuizResult bugFixQuizResult : rs) {
		
			System.out.println("Analyze made for " + bugFixQuizResult.getAuthorName() + " "
					+ bugFixQuizResult.getProject());
			System.out.println("items "+bugFixQuizResult.getResultados().size());
		
			
			for (BugFixItemResult item : bugFixQuizResult.getResultados()) {
				System.out.println("---Analyzing revision " + item.getTransactionID());
					System.out.println("----");
					
					Element revision = doc.createElement("revision");
					rootElement.appendChild(revision);
					revision.setAttribute("id",item.getTransactionID());
					
					System.out.println("Analyze made for " + bugFixQuizResult.getAuthorName() + " "
							+ bugFixQuizResult.getProject());
					System.out.println("items "+bugFixQuizResult.getResultados().size());
					
			
					for (BFPatternHunkResult hunkres : item.getAllEvaluations()) {
						
						Element hunk = doc.createElement("instance");
						String pattern = hunkres.getPattern();
						String evaluation = hunkres.getPanBugFixParttern();
						revision.appendChild(hunk);
						
						Element eval = doc.createElement("evaluation");
						eval.setTextContent(evaluation);
						hunk.appendChild(eval);
						
						Element pat = doc.createElement("pattern");
						pat.setTextContent(pattern);
						hunk.appendChild(pat);
						
					//	Element came = doc.createElement("source");
						//came.setTextContent(hunkres.getDiscoverBy());
					//	hunk.appendChild(came);
						if("Common".equals(hunkres.getDiscoverBy()))
							hunk.setAttribute("source", "AST and Token");	
							//came.setTextContent("AST and Token");
						else
							if("TokenAST".equals(hunkres.getDiscoverBy()))
								hunk.setAttribute("source", "Token");	
								//came.setTextContent("Token");
							else
								hunk.setAttribute("source", hunkres.getDiscoverBy());
								//came.setTextContent(hunkres.getDiscoverBy());
								
						/*if (hunkres.getPanBugFixParttern().equals(ResultType.BUGFIXPATTERN.toString())) {
						}*/
						
				}
					
			}
		}
		return summary;
	}
	
	public int[][] exportResult2(String cat, List<BugFixQuizResult> rs, Element rootElement, Document doc) {
		int[][] summary = new int[3][3];
		for (BugFixQuizResult bugFixQuizResult : rs) {
		
			System.out.println("Analyze made for " + bugFixQuizResult.getAuthorName() + " "
					+ bugFixQuizResult.getProject());
			System.out.println("items "+bugFixQuizResult.getResultados().size());
		
			
			for (BugFixItemResult item : bugFixQuizResult.getResultados()) {
					System.out.println("---Analyzing revision " + item.getTransactionID());
					System.out.println("----");
					
					Element revision = doc.createElement("revision");
					revision.setAttribute("id",item.getTransactionID());
					
					System.out.println("Analyze made for " + bugFixQuizResult.getAuthorName() + " "
							+ bugFixQuizResult.getProject());
					System.out.println("items "+bugFixQuizResult.getResultados().size());
					
					boolean add = false;
					for (BFPatternHunkResult hunkres : item.getAllEvaluations()) {
						
						if((!"Common".equals(hunkres.getDiscoverBy()) && !"AST".equals(hunkres.getDiscoverBy())) ){
							continue;
						}
						
						String pat_id = hunkres.getPattern().split("_")[0].split(" ")[0];
						System.out.println("hunk "+pat_id);
						if(pat_id.equals(cat) || ( cat.length()>5 && pat_id.length() > 5 && cat.substring(0, 4).equals(pat_id.substring(0, 4)))){
							if(!add){
								System.out.println("Creating ");
							add = true;
							rootElement.appendChild(revision);
							}
						Element hunk = doc.createElement("instance");
						String pattern = hunkres.getPattern();
						String evaluation = hunkres.getPanBugFixParttern();
						revision.appendChild(hunk);
						
						Element eval = doc.createElement("evaluation");
						eval.setTextContent(evaluation);
						hunk.appendChild(eval);
						
						Element pat = doc.createElement("pattern");
						pat.setTextContent(pattern);
						hunk.appendChild(pat);
						
						}
						
				}
					
			}
		}
		return summary;
	}
}
