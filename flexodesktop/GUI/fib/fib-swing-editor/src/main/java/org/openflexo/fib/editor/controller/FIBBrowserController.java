package org.openflexo.fib.editor.controller;

import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;

public class FIBBrowserController extends FIBController implements Observer {

	private static final Logger logger = Logger.getLogger(FIBBrowserController.class.getPackage().getName());

	private FIBEditorController editorController;

	public FIBBrowserController(FIBComponent rootComponent, FIBEditorController editorController) {
		super(rootComponent);
		this.editorController = editorController;
		editorController.addObserver(this);
	}

	public FIBComponent getSelectedComponent() {
		return editorController.getSelectedObject();
	}

	public void setSelectedComponent(FIBComponent selectedComponent) {
		// logger.info("setSelectedComponent with " + selectedComponent);
		editorController.setSelectedObject(selectedComponent);
	}

	public ImageIcon iconFor(FIBComponent component) {
		if (component == null) {
			return null;
		}
		if (component.isRootComponent()) {
			return FIBEditorIconLibrary.ROOT_COMPONENT_ICON;
		} else if (component instanceof FIBTabPanel) {
			return FIBEditorIconLibrary.TABS_ICON;
		} else if (component instanceof FIBPanel) {
			return FIBEditorIconLibrary.PANEL_ICON;
		} else if (component instanceof FIBSplitPanel) {
			return FIBEditorIconLibrary.SPLIT_PANEL_ICON;
		} else if (component instanceof FIBCheckBox) {
			return FIBEditorIconLibrary.CHECKBOX_ICON;
		} else if (component instanceof FIBLabel) {
			return FIBEditorIconLibrary.LABEL_ICON;
		} else if (component instanceof FIBTable) {
			return FIBEditorIconLibrary.TABLE_ICON;
		} else if (component instanceof FIBBrowser) {
			return FIBEditorIconLibrary.TREE_ICON;
		} else if (component instanceof FIBTextArea) {
			return FIBEditorIconLibrary.TEXTAREA_ICON;
		} else if (component instanceof FIBTextField) {
			return FIBEditorIconLibrary.TEXTFIELD_ICON;
		} else if (component instanceof FIBNumber) {
			return FIBEditorIconLibrary.NUMBER_ICON;
		} else if (component instanceof FIBDropDown) {
			return FIBEditorIconLibrary.DROPDOWN_ICON;
		} else if (component instanceof FIBRadioButtonList) {
			return FIBEditorIconLibrary.RADIOBUTTON_ICON;
		} else if (component instanceof FIBButton) {
			return FIBEditorIconLibrary.BUTTON_ICON;
		} else if (component instanceof FIBReferencedComponent) {
			return FIBEditorIconLibrary.REFERENCE_COMPONENT_ICON;
		}
		return null;

	}

	public String textFor(FIBComponent component) {
		if (component == null) {
			return null;
		}
		if (component.getName() != null) {
			return component.getName() + " (" + component.getClass().getSimpleName() + ")";
		} else if (component.getIdentifier() != null) {
			return component.getIdentifier() + " (" + component.getClass().getSimpleName() + ")";
		} else {
			return "<" + component.getClass().getSimpleName() + ">";
		}
	}

	public void rightClick(FIBComponent component, MouseEvent event) {
		editorController.getContextualMenu().displayPopupMenu(component, getRootView().getJComponent(), event);
	}

}
