/**
 * 
 */
package edu.nau.communicationdesigner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.diagram.ui.services.layout.LayoutType;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.nau.communicationdesigner.fileprocessing.InformationRetention;
import edu.nau.communicationdesigner.fileprocessing.InputXmiFileProcessing;
import edu.nau.communicationdesigner.fileprocessing.OutputXmiFileProcessing;
import edu.nau.communicationdesigner.fileprocessing.ResultingXmiFileProcessing;
import edu.nau.communicationdesigner.fileprocessing.XmiFileProcessing;
import edu.nau.communicationdesigner.fileprocessing.XmlDocumentCreationResolution;

import com.ibm.xtools.modeler.ui.UMLModeler;
import com.ibm.xtools.uml.ui.diagram.IUMLDiagramHelper;
import com.ibm.xtools.umlnotation.UMLDiagramKind;


/**
 * Transforms a source model into a target model using mediniQVT, IBM Rational Software Architect and DOM Parser
 * @author Kyryl Shestakov
 *
 */
public class MediniQvtRsaDomModelTransformation extends
		ModelTransformationTemplate {
	private List<Node> sourceModelPrologNodes;

	@Override
	public void obtainTargetModelWithDiagram(
			final String temporaryTargetModelPlaceholderLocation,
			String targetModelLocation) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		XmiFileProcessing resultingXmiFileProcessor = new ResultingXmiFileProcessing();
		XmlDocumentCreationResolution xmlDocumentCreationResolver =  new XmlDocumentCreationResolution() {

			@Override
			public Document resolve(DocumentBuilder documentBuilder) throws SAXException, IOException  {
				//target model needs to be parsed from a target model placeholder
				File outputModelFile = new File(temporaryTargetModelPlaceholderLocation);
				Document parsedDocument = documentBuilder.parse(outputModelFile);
				return parsedDocument;
			}
			
		};
		InformationRetention informationRetainer = new InformationRetention() {

			@Override
			public void release(Document document) {
				//there is a need to insert retained source model prolog nodes into a target model
				Node refNode = document.getDocumentElement();
				Node currentNode;
				Node importedNode;
				int count = MediniQvtRsaDomModelTransformation.this.sourceModelPrologNodes.size();
				
				for (int i=count-1; i>=0; --i){
					currentNode = sourceModelPrologNodes.get(i);
					importedNode = document.importNode(currentNode, false);
					document.insertBefore(importedNode, refNode);
					refNode = importedNode;
				}
			}

			@Override
			public void retain(Document document) {
				//there is nothing to retain from a target model
			}
			
		};
		
		processModel(resultingXmiFileProcessor, xmlDocumentCreationResolver, targetModelLocation, informationRetainer);
		createDiagramFromModel(targetModelLocation);
	}
	
	/**
	 * Creates a diagram in a specified model
	 * @param targetModelLocation location of a target model 
	 * @throws IOException 
	 */
	protected void createDiagramFromModel(final String targetModelLocation) throws IOException {
		String label = "Generate Class Diagram";
		final org.eclipse.uml2.uml.Element modelResource = UMLModeler.openModelResource(targetModelLocation);
		//TransactionalEditingDomain editDomain = UMLModeler.getEditingDomain();
        TransactionalEditingDomain editDomain = TransactionUtil.getEditingDomain(modelResource);
		
		Command layoutCommand = new RecordingCommand(editDomain, label) {
			protected void doExecute() {
				try {
					IUMLDiagramHelper helper = UMLModeler.getUMLDiagramHelper();
					Diagram diagram = helper.createDiagram(
							modelResource.getNearestPackage(), 
							UMLDiagramKind.CLASS_LITERAL,
							modelResource);
					diagram.setName("ClassDiagram");

					EList<org.eclipse.uml2.uml.Element> ownedElements = modelResource.allOwnedElements();
					for (int i=0; i<ownedElements.size(); ++i) {
						org.eclipse.uml2.uml.Element ownedElement = ownedElements.get(i);
						helper.createNode(diagram, ownedElement);
						/*EList<org.eclipse.uml2.uml.Relationship> relationships = ownedElement.getRelationships();
						for (int j=0; j<relationships.size(); ++j) {
							helper.createEdges(diagram, relationships.get(j));
						}*/
					}

					UMLModeler.getUMLDiagramHelper().layout(diagram, LayoutType.DEFAULT);
					UMLModeler.saveModelResource(modelResource);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
        editDomain.getCommandStack().execute(layoutCommand);
	}

	@Override
	public void prepareSourceModelPlaceholder(
			final String sourceModelLocation,
			String temporarySourceModelPlaceholderLocation
			) throws ParserConfigurationException, SAXException, IOException, TransformerException  {
		XmiFileProcessing inputXmiFileProcessor = new InputXmiFileProcessing();
		XmlDocumentCreationResolution xmlDocumentCreationResolver =  new XmlDocumentCreationResolution() {

			@Override
			public Document resolve(DocumentBuilder documentBuilder) throws SAXException, IOException  {
				//source model placeholder needs to be parsed from a source model
				File inputModelFile = new File(sourceModelLocation);
				Document parsedDocument = documentBuilder.parse(inputModelFile);
				return parsedDocument;
			}
			
		};
		InformationRetention informationRetainer = new InformationRetention(){

			@Override
			public void release(Document document) {
				//there is nothing to release into the source model 
			}

			@Override
			public void retain(Document document) {
				//there is a need to retain source model prolog nodes
				List<Node> retainedNodes = new ArrayList<Node>();
				//com.ibm.xtools.emf.core.signature processing instructions disappear after the transformation
				retainedNodes = getPrologNodes(document);
				removePrologNodes(document);
				MediniQvtRsaDomModelTransformation.this.sourceModelPrologNodes = retainedNodes;
			}
			
			/**
			 * Gets unused nodes in a prolog
			 * @param dom an XML document
			 */
			private List<Node> getPrologNodes(Document dom) {
				NodeList domChildren = dom.getChildNodes();
				Element rootElement = dom.getDocumentElement();
				List<Node> prologNodes = new ArrayList<Node>();
				
				int count = domChildren.getLength();
				for (int i=0; i<count; ++i) {
					if (!domChildren.item(i).equals(rootElement)) {
						prologNodes.add(domChildren.item(i));
					}
				}
				
				return prologNodes;
			}
			
			/**
			 * Removes prolog nodes
			 * @param dom an XML document
			 */
			private void removePrologNodes(Document dom) {
				NodeList domChildren = dom.getChildNodes();
				Element rootElement = dom.getDocumentElement();
				
				while (domChildren.getLength() != 1) {
					if (!domChildren.item(0).equals(rootElement)) {
						dom.removeChild(domChildren.item(0));
					}
				}
			}
			
		};
		//this.nodeRetainer = informationRetainer;
		
		processModel(inputXmiFileProcessor, xmlDocumentCreationResolver, temporarySourceModelPlaceholderLocation, informationRetainer);
	}

	@Override
	public void prepareTargetModelPlaceholder(
			String temporaryTargetModelPlaceholderLocation) throws ParserConfigurationException, SAXException, IOException, TransformerException  {
		XmiFileProcessing outputXmiFileProcessor = new OutputXmiFileProcessing();
		XmlDocumentCreationResolution xmlDocumentCreationResolver =  new XmlDocumentCreationResolution() {

			@Override
			public Document resolve(DocumentBuilder documentBuilder) {
				//target model placeholder requires an empty root element
				Document newDocument = documentBuilder.newDocument();
				return newDocument;
			}
			
		};
		InformationRetention informationRetainer = new InformationRetention() {

			@Override
			public void release(Document document) {
				//there is nothing to release into an empty target model placeholder
			}

			@Override
			public void retain(Document document) {
				//there is nothing to retain from an empty target model placeholder 
			}
			
		};
		
		processModel(outputXmiFileProcessor, xmlDocumentCreationResolver, temporaryTargetModelPlaceholderLocation, informationRetainer);
	}
	
	/**
	 * Defines an algorithm for document creation (reading), editing and writing
	 * @param xmiFileProcessor defines how to process a document
	 * @param resolver determines how to create a document
	 * @param outputFileLocation determines where to write edited document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	protected void processModel(
			XmiFileProcessing xmiFileProcessor, 
			XmlDocumentCreationResolution resolver, 
			String outputFileLocation,
			InformationRetention informationRetainer
			) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		Document document = xmiFileProcessor.createXmlDocument(resolver);
		xmiFileProcessor.editXmlDocument(document, informationRetainer);
		xmiFileProcessor.writeXmlToXmiFile(outputFileLocation, document);
	}
}
