package org.openflexo.fib.editor.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.InformationIssue;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationWarning;

public class FIBValidationController extends FIBController {

	static final Logger logger = Logger.getLogger(FIBValidationController.class.getPackage().getName());

	private FIBEditorController editorController;
	private ValidationIssue selectedValidationIssue;

	public FIBValidationController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	public FIBValidationController(FIBComponent rootComponent, FIBEditorController editorController) {
		super(rootComponent);
		this.editorController = editorController;
	}

	public ValidationIssue getSelectedValidationIssue() {
		return selectedValidationIssue;
	}

	public void setSelectedValidationIssue(ValidationIssue validationIssue) {
		selectedValidationIssue = validationIssue;
		if (validationIssue != null && validationIssue.getObject() instanceof FIBComponent) {
			editorController.setSelectedObject((FIBComponent) validationIssue.getObject());
		}
	}

	public ImageIcon iconFor(Object validationObject) {
		if (validationObject instanceof ValidationError) {
			if (((ValidationError) validationObject).isFixable()) {
				return FIBEditorIconLibrary.FIXABLE_ERROR_ICON;
			} else {
				return FIBEditorIconLibrary.UNFIXABLE_ERROR_ICON;
			}
		} else if (validationObject instanceof ValidationWarning) {
			if (((ValidationWarning) validationObject).isFixable()) {
				return FIBEditorIconLibrary.FIXABLE_WARNING_ICON;
			} else {
				return FIBEditorIconLibrary.UNFIXABLE_WARNING_ICON;
			}
		} else if (validationObject instanceof InformationIssue) {
			return FIBEditorIconLibrary.INFO_ISSUE_ICON;
		} else if (validationObject instanceof FixProposal) {
			return FIBEditorIconLibrary.FIX_PROPOSAL_ICON;
		}
		return null;
	}

	public void checkAgain() {
		if (getValidatedComponent() != null) {
			logger.info("Revalidating component " + getValidatedComponent());
			setDataObject(getValidatedComponent().validate());
		}
	}

	public void fixIssue(FixProposal fixProposal) {
		if (fixProposal != null) {
			fixProposal.apply(true);
			setDataObject(getDataObject(), true);
		}
	}

	public FIBComponent getValidatedComponent() {
		if (getDataObject() instanceof ValidationReport) {
			if (((ValidationReport) getDataObject()).getRootObject() instanceof FIBComponent) {
				return (FIBComponent) ((ValidationReport) getDataObject()).getRootObject();
			}
		}
		return null;
	}
}
