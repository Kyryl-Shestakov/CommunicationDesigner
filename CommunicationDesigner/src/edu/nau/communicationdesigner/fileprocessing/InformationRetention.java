/**
 * 
 */
package edu.nau.communicationdesigner.fileprocessing;

import org.w3c.dom.Document;

/**
 * Provides methods to retain and release information
 * @author Kyryl Shestakov
 *
 */
public interface InformationRetention {
	/**
	 * Retains information from the document
	 * @param document a document to be considered
	 */
	public void retain(Document document);
	/**
	 * Releases information to the document
	 * @param document a document to be considered
	 */
	public void release(Document document);
}
