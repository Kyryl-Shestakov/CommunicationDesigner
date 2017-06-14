package communicationdesigner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class BrowseClickHandling extends SelectionAdapter {
	private Shell shell;
	private TransformationResourceSpecifiable specifier;
	private String projectLocation;
	private String extension;
	private FieldTextSettingResolution resolver;
	
	public BrowseClickHandling(
			Shell shell, 
			TransformationResourceSpecifiable specifier, 
			FieldTextSettingResolution resolver, 
			String projectLocation, 
			String extension
			) {
		this.shell = shell;
		this.specifier = specifier;
		this.resolver = resolver;
		this.projectLocation = projectLocation;
		this.extension = extension;
	}
	
	@Override
    public void widgetSelected(SelectionEvent e) {
		FileDialog dialog = new FileDialog(this.shell, SWT.OPEN);
		dialog.setFilterExtensions(new String [] {this.extension});
		dialog.setFilterPath(this.projectLocation);
		/*String result = */dialog.open();
		String fileName = dialog.getFileName();
		this.resolver.resolve(this.specifier, fileName);
    }
}
