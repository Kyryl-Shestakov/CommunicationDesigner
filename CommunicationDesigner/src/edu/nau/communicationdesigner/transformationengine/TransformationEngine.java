/**
 * 
 */
package edu.nau.communicationdesigner.transformationengine;

/**
 * Represents actions of a transformation engine
 * @author Kyryl Shestakov
 *
 */
public interface TransformationEngine {
	public void transform (
			String temporarySourceModelPlaceholderLocation,
			String temporaryTargetModelPlaceholderLocation
			);
	
}
