/**
 * 
 */
package edu.nau.communicationdesigner.fileprocessing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Represents editing of an XML document for an output XMI model file
 * @author Kyryl Shestakov
 *
 */
public class OutputXmiFileProcessing extends XmiFileProcessing {
	@Override
	public void editXmlDocument(Document document, InformationRetention retainer) {
		//Creation of an empty xmi:XMI root tag for future uml:Model
		Element rootElement = document.createElementNS("http://www.omg.org/XMI", "xmi:XMI");
		rootElement.setAttributeNS("http://www.omg.org/XMI", "xmi:version", "2.0");
		rootElement.setAttribute("xmlns:xmi", "http://www.omg.org/XMI");
		document.appendChild(rootElement);
		
		//Create empty Model element similar to <uml:Model xmi:version="2.1" xmlns:xmi="http://schema.omg.org/spec/XMI/2.1" xmlns:uml="http://www.eclipse.org/uml2/3.0.0/UML" xmi:id="_dIsxoBVsEeeUWMz1JH_75w"></uml:Model>
		/*Element rootElement = document.createElementNS("http://www.eclipse.org/uml2/3.0.0/UML", "uml:Model");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:uml", "http://www.eclipse.org/uml2/3.0.0/UML");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xmi", "http://schema.omg.org/spec/XMI/2.1");
		rootElement.setAttributeNS("http://schema.omg.org/spec/XMI/2.1", "xmi:version", "2.1");
		document.appendChild(rootElement);*/
		
	}

}
