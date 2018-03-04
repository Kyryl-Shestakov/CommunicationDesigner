package communicationdesigner.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;

import communicationdesigner.TransformationDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TransformationHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public TransformationHandler() {
	}

	/**
	 * The command has been executed, so extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		final IPath location;
		
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
        Object firstElement = selection.getFirstElement();
        if (firstElement instanceof IAdaptable)
        {
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
            //IPath path = project.getFullPath();
            location = project.getLocation();
        } else {
        	MessageDialog.openInformation(
    				window.getShell(),
    				"Project Selection",
    				"Please, select a project");
        	return null;
        }
		
		Dialog dialog = new TransformationDialog(window.getShell(), location.toString());
		
		dialog.create();
		
		if (dialog.open() == Window.OK) {
		}
		
		return null;
	}
}
