package fr.inria.sacha.quizBFP.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.inria.sasha.utils.QueryWarehouse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.dev.util.collect.HashMap;

import au.com.bytecode.opencsv.CSVReader;

public class XMLTransactionGeneration {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("survey");
		doc.appendChild(rootElement);
		
		CSVReader reader = new CSVReader(new FileReader("c:/Temp/survey_bugfix.csv"), ';');
		String[] nextLine;
		
		 Map<String, String[]> dd = new HashMap<String, String[]>();//analyzeProjectNames();
		
		while ((nextLine = reader.readNext()) != null) {
			
			dd.put(nextLine[0],nextLine);
			
		}
		 
		int i = 0;
	
		Map<String,String> result = new HashMap<String, String>();
		File dirProj = new File("C:\\Personal\\develop\\workspaceEvolution\\surveyGumTree\\war\\datasets\\");
		for(File file :dirProj.listFiles()){
			
			if(!file.getName().endsWith("1SC")){
				continue;
			}
				
			Element projectElement = doc.createElement("project");
			rootElement.appendChild(projectElement);
			projectElement.setAttribute("name", file.getName());
			
			for(File trans : file.listFiles()){
			
				String id = trans.getName().replace("t_", "");
				createTransactionEntry(doc, projectElement,dd.get(id));
			}
		}
		
			
			// nextLine[] is an array of values from the line
			// System.out.println(nextLine[0] + nextLine[1] + "etc...");
		//	i++;
		//}
		System.out.println(" total "+i+ " "+ reader.readAll().size());
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult resultxml = new StreamResult(System.out);
			transformer.transform(source, resultxml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void createTransactionEntry(Document doc, Element rootElement, String[] nextLine) {
		List<Object[]> data =  QueryWarehouse.getRevisionwithSourceForTransaction2(Integer.valueOf(nextLine[0]));
		if(data.size() == 0){
			//	throw new Exception("Size Rev != 1");
				System.out.println("Size Rev == 0: "+nextLine[0]);
		//		continue;
				return;
		}
		
		if(data.size() > 1){
		//	throw new Exception("Size Rev != 1");
			System.out.println("Size Rev != 1: "+nextLine[0]);
		}
		
		Object[] r1 = data.get(0);
		String filename  = r1[0].toString(); 
		String number = r1[1].toString(); 
		String date = r1[5].toString();
		
		Element transaction = doc.createElement("transaction");
		rootElement.appendChild(transaction);
		transaction.setAttribute("id", nextLine[0]);
		transaction.setAttribute("rev_file", filename);
		transaction.setAttribute("rev_number", number);
		transaction.setAttribute("date", date);
		
		//--
		Element a1 = doc.createElement("rater");
		transaction.appendChild(a1);
		a1.setTextContent(nextLine[1]);
		a1.setAttribute("id", "1");
		
		Element a2 = doc.createElement("rater");
		transaction.appendChild(a2);
		a2.setTextContent(nextLine[2]);
		a2.setAttribute("id", "2");
		
		Element a3 = doc.createElement("rater");
		transaction.appendChild(a3);
		a3.setAttribute("id", "3");
		a3.setTextContent(nextLine[3]);
	}

	public static Map<String, String> analyzeProjectNames(){
		Map<String,String> result = new HashMap<String, String>();
		File dirProj = new File("");
		for(File file :dirProj.listFiles()){
			
			for(File trans : file.listFiles()){
				result.put(trans.getName().replace("t_", ""), file.getName());
			}
			
		}
		
		return result;
	}
	
}
