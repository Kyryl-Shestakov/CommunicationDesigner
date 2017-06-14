/**
 * 
 */
package edu.nau.communicationdesigner.fileprocessing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents editing of an XML document obtained from an output XMI model file
 * @author Kyryl Shestakov
 *
 */
public class ResultingXmiFileProcessing extends XmiFileProcessing {
	@Override
	public void editXmlDocument(Document document, InformationRetention retainer) {
		/*Element rootElement = document.getDocumentElement();
		modifyRootTagForVisualization(rootElement);
		retainer.release(document);*/
	}

	/**
	 * Removes unused attributes and sets used attributes appropriately
	 * @param rootElement a root element of an XML document
	 */
	private void modifyRootTagForVisualization(Element rootElement) {
		/*rootElement.removeAttribute("xmi:version");
		
		rootElement.removeAttribute("xmlns:xmi");
		rootElement.removeAttribute("xmlns:xsi");
		rootElement.removeAttribute("xmlns:uml");
		
		//String id = rootElement.getAttribute("xmi:id");
		String name = rootElement.getAttribute("name");
		
		//rootElement.removeAttribute("xmi:id");
		rootElement.removeAttribute("name");

		rootElement.setAttribute("xmlns:uml", "http://www.eclipse.org/uml2/2.1.0/UML");
		rootElement.setAttribute("xmlns:xmi", "http://www.omg.org/XMI");
		
		rootElement.setAttributeNS("http://schema.omg.org/spec/XMI/2.1", "xmi:version", "2.0");
		//rootElement.setAttributeNS("http://schema.omg.org/spec/XMI/2.1", "xmi:id", id);
		
		rootElement.setAttribute("name", name);*/
	}
	
	/*@Override
	public void writeXmlToXmiFile(String outputModelFileLocation, Document document) throws TransformerException, IOException {
		super.writeXmlToXmiFile(outputModelFileLocation, document);
		
		FileReader fileReader = new FileReader(outputModelFileLocation);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String inputLine;
		
		List<String> lines = new ArrayList<String>();
		
		while ((inputLine = bufferedReader.readLine()) != null) {
			if (!inputLine.trim().isEmpty()) {
				lines.add(inputLine);
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
	}*/
}
