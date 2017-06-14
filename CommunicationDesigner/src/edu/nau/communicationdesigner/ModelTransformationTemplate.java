/**
 * 
 */
package edu.nau.communicationdesigner;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import edu.nau.communicationdesigner.transformationengine.TransformationEngine;

/**
 * Represents a template skeleton for a source model transformation process
 * @author Kyryl Shestakov
 *
 */
public abstract class ModelTransformationTemplate {
	/**
	 * Transform a source model into a target model
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public final void transformSourceModel(
			String sourceModelLocation,
			String targetModelLocation,
			TransformationEngine transformationEngine,
			String temporarySourceModelPlaceholderLocation,
			String temporaryTargetModelPlaceholderLocation
			) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		this.prepareSourceModelPlaceholder(
				sourceModelLocation, 
				temporarySourceModelPlaceholderLocation);
		this.prepareTargetModelPlaceholder(temporaryTargetModelPlaceholderLocation);
		this.performTransformation(
				transformationEngine, 
				temporarySourceModelPlaceholderLocation, 
				temporaryTargetModelPlaceholderLocation);
		this.obtainTargetModelWithDiagram(
				temporaryTargetModelPlaceholderLocation, 
				targetModelLocation);
	}
	
	/**
	 * Creates source model placeholder file from a source model 
	 * @param sourceModelLocation location of a source model file
	 * @param temporarySourceModelPlaceholderLocation location for a source model placeholder file
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public abstract void prepareSourceModelPlaceholder(String sourceModelLocation, String temporarySourceModelPlaceholderLocation) throws ParserConfigurationException, SAXException, IOException, TransformerException;
	
	/**
	 * Creates target model placeholder file 
	 * @param temporaryTargetModelPlaceholderLocation location for a target model placeholder file
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public abstract void prepareTargetModelPlaceholder(String temporaryTargetModelPlaceholderLocation) throws ParserConfigurationException, SAXException, IOException, TransformerException;
	
	
	
	/**
	 * Transforms temporary source model into temporary target model
	 */
	public void performTransformation(
			TransformationEngine transformationEngine, 
			String temporarySourceModelPlaceholderLocation,
			String temporaryTargetModelPlaceholderLocation
			) {
		transformationEngine.transform(
				temporarySourceModelPlaceholderLocation, 
				temporaryTargetModelPlaceholderLocation);
	}
	
	/**
	 * Creates resulting target model file with a diagram from an obtained target model placeholder 
	 * @param temporaryTargetModelPlaceholderLocation location of a target model placeholder file
	 * @param targetModelLocation location for a target model file
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public abstract void obtainTargetModelWithDiagram(String temporaryTargetModelPlaceholderLocation, String targetModelLocation) throws ParserConfigurationException, SAXException, IOException, TransformerException;
}
