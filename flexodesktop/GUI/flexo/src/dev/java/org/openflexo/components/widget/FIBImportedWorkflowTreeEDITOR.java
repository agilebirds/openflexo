package org.openflexo.components.widget;

import java.io.File;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoFIBController;

public class FIBImportedWorkflowTreeEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		return new Object[] { null };
	}

	@Override
	public File getFIBFile() {
		return new FileResource("Fib/FIBImportedWorkflowTree.fib");
	}

	@Override
	public FIBController makeNewController(FIBComponent component) {
		return new FlexoFIBController(component);
	}

	public static void main(String[] args) {
		main(FIBImportedWorkflowTreeEDITOR.class);
	}

}
