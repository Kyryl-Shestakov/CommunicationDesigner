package communicationdesigner;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import communicationdesigner.handlers.TransformationThread;

public class TransformationDialog extends Dialog implements TransformationResourceSpecifiable {
	
	private Text transformationScript;
    private Text inputModelFile;
    
    private Shell shell;
    private String projectLocation;

	public TransformationDialog(Shell parentShell, String projectLocation) {
		super(parentShell);
		this.shell = parentShell;
		this.projectLocation = projectLocation;
	}
	
	@Override
	public void create() {
	    super.create();
	    //getButton(IDialogConstants.OK_ID).setVisible(false);
	}

	
	@Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(3, false);
        container.setLayout(layout);
        
        createTransformationScriptField(container);
        createInputModelField(container);

        return area;
    }

    // overriding this methods allows you to set the
    // title of the custom dialog
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Transformation Definition");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 300);
    }
    
    private void createTransformationScriptField(Composite container) {
    	Label transformationScriptLabel = new Label(container, SWT.NONE);
        transformationScriptLabel.setText("QVT-R script:");

        GridData dataTransformationScript = new GridData();
        dataTransformationScript.grabExcessHorizontalSpace = true;
        dataTransformationScript.horizontalAlignment = GridData.FILL;

        transformationScript = new Text(container, SWT.BORDER);
        transformationScript.setLayoutData(dataTransformationScript);
        
        Button button = new Button(container, SWT.PUSH);
        button.setLayoutData(new GridData(GridData.FILL, SWT.CENTER, false,
                false));
        button.setText("Browse...");
        FieldTextSettingResolution transformationScriptResolver = new FieldTextSettingResolution() {

			@Override
			public void resolve(TransformationResourceSpecifiable specifier,
					String text) {
				specifier.setQvtScriptPath(text);
			}
        	
        };
        button.addSelectionListener(new BrowseClickHandling(
        		this.shell, 
        		this, 
        		transformationScriptResolver, 
        		this.projectLocation, 
        		"*.qvt"));
    }
    
    private void createInputModelField(Composite container) {
    	Label inputModelLabel = new Label(container, SWT.NONE);
    	inputModelLabel.setText("Input model:");

        GridData dataInputModel = new GridData();
        dataInputModel.grabExcessHorizontalSpace = true;
        dataInputModel.horizontalAlignment = GridData.FILL;

        inputModelFile = new Text(container, SWT.BORDER);
        inputModelFile.setLayoutData(dataInputModel);
        
        Button button = new Button(container, SWT.PUSH);
        button.setLayoutData(new GridData(GridData.FILL, SWT.CENTER, false,
                false));
        button.setText("Browse...");
        FieldTextSettingResolution inputModelFileResolver = new FieldTextSettingResolution() {

			@Override
			public void resolve(TransformationResourceSpecifiable specifier,
					String text) {
				specifier.setInputModelPath(text);
			}
        	
        };
        button.addSelectionListener(new BrowseClickHandling(
        		this.shell, 
        		this, 
        		inputModelFileResolver, 
        		this.projectLocation, 
        		"*.emx"));
    }
    
    @Override
    protected void okPressed() {
    	String root = projectLocation + "/";
    	String sourceModelName = getInputModelPath();
    	String qvtScriptName = getQvtScriptPath();
    	
    	File qvtFolder = new File(root + "qvt");

    	if (!qvtFolder.exists()) {
    		qvtFolder.mkdir();
    	}
    	
    	File qvtFile = new File(root + "qvt/" + qvtScriptName);
    	
    	if (!qvtFile.exists() || !qvtFile.isFile()) {
    		MessageDialog.openInformation(
        			shell,
        			"File Info",
        			"Transformation script does not exist.");
    		return;
    	}
    	
    	File modelFile = new File(root + sourceModelName);

    	if (!modelFile.exists() || !modelFile.isFile()) {
    		MessageDialog.openInformation(
    				shell,
    				"File Info",
    				"Input model file does not exist.");
    		return;
    	}
    	
    	File tracesFolder = new File(root + "traces");
    	
    	if (!tracesFolder.exists()) {
    		tracesFolder.mkdir();
    	}

    	File tempFolder = new File(root + "temp");

    	if (!tempFolder.exists()) {
    		tempFolder.mkdir();
    	}
    	
    	Thread thread = new TransformationThread(
    			"Transformation Execution", 
    			root, sourceModelName, 
    			qvtScriptName);
    	
    	BusyIndicator.showWhile(shell.getDisplay(), thread);
    	
    	MessageDialog.openInformation(
    			shell,
    			"Result",
    			"Transformation Complete.");
    	
    	TransformationDialog.this.close();
    }

	@Override
	public void setInputModelPath(String inputModelPath) {
		inputModelFile.setText(inputModelPath);
		
	}

	@Override
	public void setQvtScriptPath(String qvtScriptPath) {
		transformationScript.setText(qvtScriptPath);
	}

	@Override
	public String getInputModelPath() {
		return this.inputModelFile.getText();
	}

	@Override
	public String getQvtScriptPath() {
		return this.transformationScript.getText();
	}

}

