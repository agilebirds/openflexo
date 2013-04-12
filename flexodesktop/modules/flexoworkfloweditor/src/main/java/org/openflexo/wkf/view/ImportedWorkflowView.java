package org.openflexo.wkf.view;

import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FIBBrowserView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.ControllerModel;

public class ImportedWorkflowView extends FIBBrowserView<ControllerModel> {
	private static final FileResource FIB_FILE = new FileResource("Fib/FIBImportedWorkflowTree.fib");

	public ImportedWorkflowView(FlexoController controller) {
		super(controller.getControllerModel(), controller, FIB_FILE);
	}

	public void setSelected(FlexoModelObject object) {
		getBrowserWidget().setSelectedObject(object);
	}

	private FIBBrowserWidget getBrowserWidget() {
		return (FIBBrowserWidget) getFIBController().viewForComponent("ImportedWorkflow");
	}

}
