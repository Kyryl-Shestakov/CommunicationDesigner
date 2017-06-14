package communicationdesigner.handlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

import edu.nau.communicationdesigner.MediniQvtRsaDomModelTransformation;
import edu.nau.communicationdesigner.ModelTransformationTemplate;
import edu.nau.communicationdesigner.transformationengine.MediniQvtTransformationEngine;
import edu.nau.communicationdesigner.transformationengine.TransformationEngine;

public class TransformationThread extends Thread {
	private String root;
	private String sourceModelName;
	private String qvtScriptName;
	public TransformationThread(String name, String root, String sourceModelName, String qvtScriptName) {
		super(name);
		this.root = root;
		this.sourceModelName = sourceModelName;
		this.qvtScriptName = qvtScriptName;
	}

	@Override
	public void run() {
		String sourceModelLocation = root + sourceModelName;
		String targetModelName = "OutputResult_from_" + sourceModelName;
		String targetModelLocation = root + targetModelName;
		String temporarySourceModelPlaceholderLocation = root + "temp/source.uml";
		String temporaryTargetModelPlaceholderLocation = root + "temp/target.uml";
		
		String qvtScriptLocation = root + "/qvt/" + qvtScriptName;
		String tracesFolderLocation = root + "traces";
		
		String declaration = "";
		
		FileReader fileReader;
		try {
			fileReader = new FileReader(qvtScriptLocation);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String inputLine = "";
			
			while (!inputLine.contains("{")) {
				inputLine = bufferedReader.readLine();
				declaration += inputLine;
			}
			
			bufferedReader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String[] parts = declaration.split("[ (:,){]");
		List<String> identifiers = new ArrayList<String>();
		
		for (int i=0; i<parts.length; ++i) {
			if (!parts[i].isEmpty()) {
				identifiers.add(parts[i]);
			}
		}
		
		String transformationName = identifiers.get(1);
		String transformationDirection = identifiers.get(4);
		
		TransformationEngine transformationEngine = new MediniQvtTransformationEngine(
				System.out,
				qvtScriptLocation,
				tracesFolderLocation,
				transformationName,
				transformationDirection);
		
		ModelTransformationTemplate modelTransformer = new MediniQvtRsaDomModelTransformation();
		try {
			modelTransformer.transformSourceModel(
					sourceModelLocation, 
					targetModelLocation, 
					transformationEngine, 
					temporarySourceModelPlaceholderLocation, 
					temporaryTargetModelPlaceholderLocation);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
