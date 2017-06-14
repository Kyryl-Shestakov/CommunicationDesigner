/**
 * 
 */
package edu.nau.communicationdesigner.transformationengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.UMLPackage;

import uk.ac.kent.cs.kmf.util.ILog;
import uk.ac.kent.cs.kmf.util.OutputStreamLog;
import de.ikv.emf.qvt.EMFQvtProcessorImpl;
import de.ikv.medini.qvt.QVTProcessorConsts;

/**
 * Performs transformations using mediniQVT
 * @author Kyryl Shestakov
 *
 */
public class MediniQvtTransformationEngine implements TransformationEngine {
	private String qvtScriptLocation;
	private String tracesFolderLocation;
	private String transformationName;
	private String transformationDirection;
	private ILog logger;

	private EMFQvtProcessorImpl processorImpl;

	protected ResourceSet resourceSet;

	/**
	 * Initializes the QVT processor
	 * @param outputStream an {@link OutputStream} for logging
	 * @param qvtScriptLocation location of a qvt script
	 * @param tracesFolderLocation location of traces folder
	 * @param transformationName name of a transformation to use from a qvt script
	 * @param transformationDirection the direction of a transformation (usually, target)
	 */
	public MediniQvtTransformationEngine(
			OutputStream outputStream,
			String qvtScriptLocation,
			String tracesFolderLocation,
			String transformationName,
			String transformationDirection
			) {
		this.logger = new OutputStreamLog(outputStream);
		this.processorImpl = new EMFQvtProcessorImpl(this.logger);
		this.processorImpl.setProperty(QVTProcessorConsts.PROP_DEBUG, "true");
		
		this.qvtScriptLocation = qvtScriptLocation;
		this.tracesFolderLocation = tracesFolderLocation;
		this.transformationName = transformationName;
		this.transformationDirection = transformationDirection;
	}

	public String getQvtScriptLocation() {
		return qvtScriptLocation;
	}

	public void setQvtScriptLocation(String qvtScriptLocation) {
		this.qvtScriptLocation = qvtScriptLocation;
	}

	public String getTracesFolderLocation() {
		return tracesFolderLocation;
	}

	public void setTracesFolderLocation(String tracesFolderLocation) {
		this.tracesFolderLocation = tracesFolderLocation;
	}

	public String getTransformationName() {
		return transformationName;
	}

	public void setTransformationName(String transformationName) {
		this.transformationName = transformationName;
	}

	public String getTransformationDirection() {
		return transformationDirection;
	}

	public void setTransformationDirection(String transformationDirection) {
		this.transformationDirection = transformationDirection;
	}

	/**
	 * Provide the information about the metamodels, which are involved in the transformation
	 * 
	 * @param ePackages
	 *            the metamodel packages
	 */
	public void init(Collection<EPackage> ePackages) {
		Iterator<EPackage> iterator = ePackages.iterator();
		while (iterator.hasNext()) {
			this.processorImpl.addMetaModel(iterator.next());
		}
	}

	private void clean() {
		this.processorImpl.setModels(Collections.EMPTY_LIST);
	}

	/**
	 * Before transforming, the engine has to know the model to perform the transformation on, as
	 * well as a directory for the traces to store.
	 * 
	 * @param modelResources
	 *            models for the execution - take care of the right order!
	 * @param workingDirectory
	 *            working directory of the QVT engine
	 */
	public void preExecution(Collection<?> modelResources, URI workingDirectory) {
		this.processorImpl.setWorkingLocation(workingDirectory);
		this.processorImpl.setModels(modelResources);
	}

	/**
	 * Transform a QVT script in a specific direction.
	 * 
	 * @param qvtRuleSet
	 *            the QVT transformation
	 * @param transformation
	 *            name of the transformation
	 * @param direction
	 *            name of the target - must conform to your QVT transformation definition
	 * @throws Throwable
	 */
	public void transform(Reader qvtRuleSet, String transformation, String direction) throws Throwable {
		this.processorImpl.evaluateQVT(qvtRuleSet, transformation, true, direction, new Object[0], null, this.logger);
		this.clean();
	}

	/**
	 * Helper for XMI loading. Does lazy loading.
	 * 
	 * @param xmlFileName
	 *            file name to load
	 * @return the EMF resource
	 */
	public Resource getResource(String xmlFileName) {
		URI uri = URI.createFileURI(xmlFileName);
		Resource resource = null;
		try {
			resource = this.resourceSet.getResource(uri, true);
		} catch (Throwable fileNotFoundException) {
			resource = this.resourceSet.createResource(uri);
		}
		return resource;
	}

	public void launch(
			String temporarySourceModelPlaceholderLocation,
			String temporaryTargetModelPlaceholderLocation
			) {

		// initialize resource set of models
		this.resourceSet = new ResourceSetImpl();
		this.resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
		    Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

		// collect all necessary packages from the metamodel(s)
		Collection<EPackage> metaPackages = new ArrayList<EPackage>();
		this.collectMetaModels(metaPackages);

		// make these packages known to the QVT engine
		this.init(metaPackages);

		// load the example model files
		Resource resource1 = this.getResource(temporarySourceModelPlaceholderLocation); // adjustable field
		Resource resource2 = this.getResource(temporaryTargetModelPlaceholderLocation); // adjustable field

		// Collect the models, which should participate in the transformation.
		// You can provide a list of models for each direction.
		// The models must be added in the same order as defined in your transformation!
		Collection<Collection<Resource>> modelResources = new ArrayList<Collection<Resource>>();
		Collection<Resource> firstSetOfModels = new ArrayList<Resource>();
		Collection<Resource> secondSetOfModels = new ArrayList<Resource>();
		modelResources.add(firstSetOfModels);
		modelResources.add(secondSetOfModels);
		firstSetOfModels.add(resource1);
		secondSetOfModels.add(resource2);

		// tell the QVT engine a directory to work in - e.g. to store the trace (meta)models
		URI directory = URI.createFileURI(this.tracesFolderLocation); // adjustable field
		this.preExecution(modelResources, directory);

		// load the QVT relations
		FileReader qvtRuleSet = null;
		try {
			qvtRuleSet = new FileReader(this.qvtScriptLocation); // adjustable field
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
			return;
		}
		// tell the QVT engine, which transformation to execute - there might be more than one in
		// the QVT file!
		String transformation = this.transformationName; // adjustable field
		// give the direction of the transformation (according to the transformation definition)
		String direction = this.transformationDirection; // adjustable field

		// just do it ;-)
		try {
			this.transform(qvtRuleSet, transformation, direction);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}

		// Note: the engine does not save the model resources, which were participating in the
		// transformation.
		// You have to take care of this.
		try {
			resource2.save(Collections.EMPTY_MAP);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Add all metamodel packages of models/qvt script
	 * 
	 * @param metaPackages
	 * @return
	 */
	protected void collectMetaModels(Collection<EPackage> metaPackages) {
		//metaPackages.add(ShapeLanguageMetamodelPackage.eINSTANCE);
		metaPackages.add(UMLPackage.eINSTANCE);
		
	}
	
	@Override
	public void transform(String temporarySourceModelPlaceholderLocation,
			String temporaryTargetModelPlaceholderLocation) {
		this.deleteTraces(this.tracesFolderLocation);
		this.launch(temporarySourceModelPlaceholderLocation, temporaryTargetModelPlaceholderLocation);
	}
	
	/**
	 * Deletes traces created by the transformation process from the traces folder
	 * @param tracesFolderLocation location of traces folder to delete traces from
	 */
	protected void deleteTraces(String tracesFolderLocation) {
		File tracesFolder = new File(tracesFolderLocation);
		File[] files = tracesFolder.listFiles();
		
		int count = files.length;
		
		for (int i=0; i<count; ++i) {
			files[i].delete();
		}
	}

}
