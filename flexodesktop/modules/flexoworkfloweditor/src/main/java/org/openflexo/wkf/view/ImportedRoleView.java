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

public class ImportedRoleView extends JPanel {
	private static final FileResource FIB_FILE = new FileResource("Fib/FIBImportedRole.fib");
	private FIBView<?, ?> importedRoleView;

	public ImportedRoleView(FlexoController controller) {
		super(new BorderLayout());
		FIBComponent comp = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
		importedRoleView = FIBController.makeView(comp, new FlexoFIBController(comp, controller));
		importedRoleView.getController().setDataObject(controller.getControllerModel());
		add(importedRoleView.getResultingJComponent());
	}

	public void setSelected(FlexoModelObject object) {
		getBrowserWidget().setSelectedObject(object);
	}

	private FIBBrowserWidget getBrowserWidget() {
		return (FIBBrowserWidget) importedRoleView.getController().viewForComponent("ImportedRoleList");
	}

}
