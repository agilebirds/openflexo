package org.openflexo.fib.editor;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;

public class ValidationWindow {

	public static File COMPONENT_VALIDATION_FIB = new FileResource("Fib/ComponentValidation.fib");

	FIBView validationView = null;
	JDialog validationDialog = null;

	public ValidationWindow(JFrame frame) {
		FIBComponent componentValidationComponent = FIBLibrary.instance().retrieveFIBComponent(COMPONENT_VALIDATION_FIB);
		validationView = FIBController.makeView(componentValidationComponent);
		validationDialog = new JDialog(frame, FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_validation"),
				false);
		validationDialog.getContentPane().add(validationView.getResultingJComponent());
		validationDialog.validate();
		validationDialog.pack();
	}

	public void validateAndDisplayReportForComponent(FIBComponent component) {
		ValidationReport report = component.validate();
		validationView.getController().setDataObject(report);
		validationDialog.setVisible(true);
	}
}