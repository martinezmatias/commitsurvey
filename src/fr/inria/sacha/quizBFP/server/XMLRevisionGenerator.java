package fr.inria.sacha.quizBFP.server;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.inria.sasha.utils.QueryWarehouse;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLRevisionGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws Exception {
	
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse("c:/tmp/out2.xml");
		
		Node survey = doc.getFirstChild();
		Node pattern = survey.getFirstChild();
		
		while(pattern != null){
		
		Node rev = pattern.getFirstChild();
	
		while(rev != null){

			NamedNodeMap earthAttributes = rev.getAttributes();
		//	ni.getAttributes().
			String id =	earthAttributes.getNamedItem("id").getTextContent();
			System.out.println();
			List<Object[]> r = QueryWarehouse.getRevision(id);
			Object[] revi = r.get(0);
			Attr name = doc.createAttribute("name");
			name.setValue(revi[0].toString());
			earthAttributes.setNamedItem(name);
					
			
			Attr rnr = doc.createAttribute("number");
			rnr.setValue(revi[1].toString());
			earthAttributes.setNamedItem(rnr);
			
			
			Attr rnl = doc.createAttribute("previous");
			rnl.setValue(revi[2].toString());
			earthAttributes.setNamedItem(rnl);
			
			
			System.out.println(r);
			rev = rev.getNextSibling();
			
		}
		pattern =pattern.getNextSibling();
		}
		
		//Attr galaxy = doc.createAttribute("galaxy");
		//galaxy.setValue("milky way");
		//earthAttributes.setNamedItem(galaxy);
	/*	Node canada = doc.createElement("country");
		canada.setTextContent("ca");
		earth.appendChild(canada);*/
		
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
	}

}
