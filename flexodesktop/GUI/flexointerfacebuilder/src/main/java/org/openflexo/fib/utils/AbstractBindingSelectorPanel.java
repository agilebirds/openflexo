package org.openflexo.fib.utils;

import org.openflexo.swing.CustomPopup.ResizablePanel;

/**
 * This is the common superclass for all panels representing a binding, in the context of BindingSelector
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractBindingSelectorPanel extends ResizablePanel {

	protected abstract void synchronizePanelWithTextFieldValue(String textValue);

	protected abstract void init();

	protected abstract void update();

	protected abstract void fireBindingDefinitionChanged();

	protected abstract void fireBindableChanged();

	protected abstract void processTabPressed();

	protected abstract void processDownPressed();

	protected abstract void processUpPressed();

	protected abstract void processLeftPressed();

	protected abstract void processRightPressed();

	protected abstract void processEnterPressed();

	protected abstract void processBackspace();

	protected abstract void processDelete();

	protected abstract void willApply();

	protected abstract void delete();

}