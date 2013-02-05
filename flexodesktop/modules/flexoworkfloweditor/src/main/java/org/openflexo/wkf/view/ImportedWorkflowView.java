package org.openflexo.wkf.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

public class ImportedWorkflowView extends JPanel {
	private static final FileResource FIB_FILE = new FileResource("Fib/FIBImportedWorkflowTree.fib");
	private FIBView<?, ?> importedWorkflowView;

	public ImportedWorkflowView(FlexoController controller) {
		super(new BorderLayout());
		FIBComponent comp = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
		importedWorkflowView = FIBController.makeView(comp, new FlexoFIBController(comp, controller));
		importedWorkflowView.getController().setDataObject(controller.getControllerModel());
		add(importedWorkflowView.getResultingJComponent());
	}

	public void setSelected(FlexoModelObject object) {
		getBrowserWidget().setSelectedObject(object);
	}

	private FIBBrowserWidget getBrowserWidget() {
		return (FIBBrowserWidget) importedWorkflowView.getController().viewForComponent("ImportedWorkflow");
	}

}
