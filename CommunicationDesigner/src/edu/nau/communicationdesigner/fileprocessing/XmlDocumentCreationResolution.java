/**
 * 
 */
package edu.nau.communicationdesigner.fileprocessing;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Resolves a creation of a {@link Document} using {@link DocumentBuilder}
 * @author Kyryl Shestakov
 *
 */
public interface XmlDocumentCreationResolution {
	/**
	 * Returns a document created using a document builder 
	 * @param documentBuilder a builder for an XML document
	 * @return created XML document
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public Document resolve(DocumentBuilder documentBuilder) throws SAXException, IOException;
}
