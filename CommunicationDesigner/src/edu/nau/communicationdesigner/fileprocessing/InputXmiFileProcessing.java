/**
 * 
 */
package edu.nau.communicationdesigner.fileprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Represents editing of an XML document obtained from an input XMI model file
 * @author Kyryl Shestakov
 *
 */
public class InputXmiFileProcessing extends XmiFileProcessing {
	@Override
	public void editXmlDocument(Document document, InformationRetention retainer) {
		
		//Element rootElement = document.getDocumentElement();
		
		retainer.retain(document);
		
		//modifyRootTagForTransformation(rootElement);
		
		//Tags packageImport and profileApplication are not recognized by mediniQVT
		//therefore they must be deleted
		//removeChildren(rootElement, "packageImport");
		//removeChildren(rootElement, "profileApplication");
		
		//eAnnotations tag must be deleted last, because profileApplication tags contain eAnnotations tags as well
		//removeChildren(rootElement, "eAnnotations");
	}
	
	/**
	 * Removes unused attributes and sets used attributes appropriately
	 * @param rootElement a root element of an XML document
	 */
	private void modifyRootTagForTransformation(Element rootElement) {
		//rootElement.removeAttribute("xmlns:ecore");
		//rootElement.removeAttribute("xmlns:notation");
		//rootElement.removeAttribute("xmlns:umlnotation");
	}
	
	/**
	 * Removes child elements by name
	 * @param root a root element of an XML document
	 * @param childName name of a child element to remove
	 */
	private void removeChildren(Element root, String childName){
		NodeList children = root.getElementsByTagName(childName);
		
		while (children.getLength() != 0){
			//System.out.println("The number of " + childName + " nodes is " + children.getLength());
			Node child = children.item(0);
			root.removeChild(child);
		}
	}
	
	@Override
	public void writeXmlToXmiFile(String outputModelFileLocation, Document document) throws TransformerException, IOException {
		super.writeXmlToXmiFile(outputModelFileLocation, document);
		
		FileReader fileReader = new FileReader(outputModelFileLocation);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String inputLine /*= bufferedReader.readLine()*/;
		/*String declaration = inputLine.substring(0, inputLine.indexOf("?>") + 2);
		String openingRootTag = inputLine.substring(inputLine.indexOf("<uml:Model"));*/
		
		List<String> lines = new ArrayList<String>();
		/*lines.add(declaration);
		lines.add(openingRootTag);*/
		
		while ((inputLine = bufferedReader.readLine()) != null) {
			if (!inputLine.trim().isEmpty()) {
				lines.add(inputLine/*.replaceAll(" xmlns:xmi=\"http://www.omg.org/XMI\"", "")*/);
			}
		}
		
		bufferedReader.close();
		
		FileWriter fileWriter = new FileWriter(outputModelFileLocation);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		int count = lines.size();
		for (int i = 0; i<count; ++i) {
			bufferedWriter.append(lines.get(i));
			bufferedWriter.newLine();
		}
		
		bufferedWriter.close();
	}
}
