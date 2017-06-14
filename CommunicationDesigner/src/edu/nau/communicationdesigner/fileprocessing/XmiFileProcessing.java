/**
 * 
 */
package edu.nau.communicationdesigner.fileprocessing;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Represents processing of XMI documents such as reading, editing and writing
 * @author Kyryl Shestakov
 *
 */
public abstract class XmiFileProcessing {
	/**
	 * Obtains XML document from a resolver
	 * @return document obtained from a resolver
	 */
	public Document createXmlDocument(XmlDocumentCreationResolution resolver) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
		domBuilderFactory.setNamespaceAware(true);
		DocumentBuilder domBuilder = domBuilderFactory.newDocumentBuilder();
		Document dom = resolver.resolve(domBuilder);
		dom.setXmlStandalone(true);
		return dom;
	}
	
	/**
	 * Edits an XML document
	 * @param document a document to be edited
	 * @param retainer retains necessary information
	 * @return a list of nodes to be retained
	 */
	public abstract void editXmlDocument(Document document, InformationRetention retainer);
	
	/**
	 * Writes edited XML document into an output XMI model file
	 * @param targetFileLocation location of an output XMI model file
	 * @param document a document built from an input XMI model file
	 * @throws IOException 
	 */
	public void writeXmlToXmiFile(String outputModelFileLocation, Document document) throws TransformerException, IOException {
		File outputModelFile = new File(outputModelFileLocation);
		DOMSource source = new DOMSource(document);
		Result result = new StreamResult(outputModelFile);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		//transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
		//transformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");
		transformer.transform(source, result);
	}
}
