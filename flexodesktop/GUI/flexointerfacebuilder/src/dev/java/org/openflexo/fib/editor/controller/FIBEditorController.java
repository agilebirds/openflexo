/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.editor.controller;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBViewFactory;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.editor.FIBGenericEditor;
import org.openflexo.fib.editor.notifications.FocusedObjectChange;
import org.openflexo.fib.editor.notifications.SelectedObjectChange;
import org.openflexo.fib.editor.view.container.FIBEditablePanelView;
import org.openflexo.fib.editor.view.container.FIBEditableTabPanelView;
import org.openflexo.fib.editor.view.container.FIBEditableTabView;
import org.openflexo.fib.editor.view.widget.FIBEditableBrowserWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableButtonWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableCheckboxListWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableCheckboxWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableColorWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableCustomWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableDropDownWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableFileWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableFontWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableHtmlEditorWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableImageWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableLabelWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableListWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableNumberWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableRadioButtonListWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableTableWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableTextAreaWidget;
import org.openflexo.fib.editor.view.widget.FIBEditableTextFieldWidget;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;

public class FIBEditorController /*extends FIBController*/extends Observable {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditorController.class.getPackage().getName());

	private final FIBController controller;

	public static File BROWSER_FIB = new FileResource("Fib/Browser.fib");

	private final JPanel editorPanel;
	private final FIBView fibPanel;
	private final FIBGenericEditor editor;

	private FIBComponent selectedObject = null;
	private FIBComponent focusedObject = null;

	private ContextualMenu contextualMenu;

	private FIBBrowserController browserController;

	public FIBEditorController(FIBComponent fibComponent, FIBGenericEditor editor) {
		this(fibComponent, editor, null);

		// Class testClass = null;
		if (fibComponent.getDataClass() != null) {
			try {
				// testClass = Class.forName(fibComponent.getDataClassName());
				Object testData = fibComponent.getDataClass().newInstance();
				fibPanel.getController().setDataObject(testData);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			fibPanel.getController().updateWithoutDataObject();
		}

	}

	public FIBEditorController(FIBComponent fibComponent, FIBGenericEditor editor, Object dataObject) {
		this(fibComponent, editor, dataObject, FIBController.instanciateController(fibComponent, FIBAbstractEditor.LOCALIZATION));
	}

	public FIBEditorController(FIBComponent fibComponent, FIBGenericEditor editor, Object dataObject, FIBController controller) {
		this.controller = controller;
		controller.setViewFactory(new EditorFIBViewFactory());

		this.editor = editor;

		contextualMenu = new ContextualMenu(this);

		addObserver(editor.getInspector());

		editorPanel = new JPanel(new BorderLayout());

		FIBComponent browserComponent = FIBLibrary.instance().retrieveFIBComponent(BROWSER_FIB, false);
		browserController = new FIBBrowserController(browserComponent, this);
		FIBView view = FIBController.makeView(browserComponent, browserController);
		view.getController().setDataObject(fibComponent);

		fibPanel = controller.buildView();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, view.getResultingJComponent(), fibPanel.getResultingJComponent()/*new JScrollPane(fibPanel.getJComponent())*/);

		editorPanel.add(splitPane, BorderLayout.CENTER);

		if (dataObject != null) {
			fibPanel.getController().setDataObject(dataObject, true);
		} else {
			fibPanel.getController().updateWithoutDataObject();
		}

	}

	public Object getDataObject() {
		return controller.getDataObject();
	}

	public void setDataObject(Object anObject) {
		controller.setDataObject(anObject);
	}

	public FIBController getController() {
		return controller;
	}

	public FIBGenericEditor getEditor() {
		return editor;
	}

	public JPanel getEditorPanel() {
		return editorPanel;
	}

	public ContextualMenu getContextualMenu() {
		return contextualMenu;
	}

	public FIBEditorPalette getPalette() {
		return editor.getPalette();
	}

	public FIBView getFibPanel() {
		return fibPanel;
	}

	public FIBComponent getFocusedObject() {
		return focusedObject;
	}

	public void setFocusedObject(FIBComponent aComponent) {
		if (aComponent != focusedObject) {
			// System.out.println("setFocusedObject with "+aComponent);
			FocusedObjectChange change = new FocusedObjectChange(focusedObject, aComponent);
			focusedObject = aComponent;
			setChanged();
			notifyObservers(change);
		}
	}

	public FIBComponent getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(FIBComponent aComponent) {
		// logger.info("setSelectedObject "+aComponent);
		if (aComponent != selectedObject) {
			SelectedObjectChange change = new SelectedObjectChange(selectedObject, aComponent);
			selectedObject = aComponent;
			setChanged();
			notifyObservers(change);
		}

		// System.out.println("set selected: "+selectedObject);

		/*if (selectedObject != null) {
			fibPanel.getController().viewForComponent(selectedObject).getJComponent().setBorder(oldBorder);
		}

		if (aComponent != null) {
			selectedObject = aComponent;
			oldBorder = fibPanel.getController().viewForComponent(aComponent).getJComponent().getBorder();
			fibPanel.getController().viewForComponent(aComponent).getJComponent().setBorder(BorderFactory.createLineBorder(Color.BLUE));
			editor.getInspector().inspectObject(aComponent);
		}
		else {
			editor.getInspector().inspectObject(null);
		}*/
	}

	public void notifyFocusedAndSelectedObject() {
		FocusedObjectChange change1 = new FocusedObjectChange(focusedObject, focusedObject);
		setChanged();
		notifyObservers(change1);
		SelectedObjectChange change2 = new SelectedObjectChange(selectedObject, selectedObject);
		setChanged();
		notifyObservers(change2);
	}

	public FIBView viewForComponent(FIBComponent component) {
		return controller.viewForComponent(component);
	}

	/*public void keyTyped(KeyEvent e)
	{
		logger.fine("keyTyped() "+e);
		if (e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
			if (getSelectedObject() != null) {
				boolean deleteIt = JOptionPane.showConfirmDialog(editor.getFrame(),
						getSelectedObject()+": really delete this component (undoable operation) ?", "information",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
				if (deleteIt) {
					logger.info("Removing object "+getSelectedObject());
					getSelectedObject().delete();
				}
			}
		}
	}*/

	public void switchToLanguage(Language language) {
		controller.switchToLanguage(language);
	}

	protected class EditorFIBViewFactory implements FIBViewFactory {
		@Override
		public FIBView makeContainer(FIBContainer fibContainer) {
			if (fibContainer instanceof FIBTab) {
				return new FIBEditableTabView((FIBTab) fibContainer, FIBEditorController.this);
			} else if (fibContainer instanceof FIBPanel) {
				return new FIBEditablePanelView((FIBPanel) fibContainer, FIBEditorController.this);
			} else if (fibContainer instanceof FIBTabPanel) {
				return new FIBEditableTabPanelView((FIBTabPanel) fibContainer, FIBEditorController.this);
			}
			return null;
		}

		@Override
		public FIBWidgetView makeWidget(FIBWidget fibWidget) {
			if (fibWidget instanceof FIBTextField) {
				return new FIBEditableTextFieldWidget((FIBTextField) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBTextArea) {
				return new FIBEditableTextAreaWidget((FIBTextArea) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBHtmlEditor) {
				return new FIBEditableHtmlEditorWidget((FIBHtmlEditor) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBLabel) {
				return new FIBEditableLabelWidget((FIBLabel) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBImage) {
				return new FIBEditableImageWidget((FIBImage) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBCheckBox) {
				return new FIBEditableCheckboxWidget((FIBCheckBox) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBTable) {
				return new FIBEditableTableWidget((FIBTable) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBBrowser) {
				return new FIBEditableBrowserWidget((FIBBrowser) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBDropDown) {
				return new FIBEditableDropDownWidget((FIBDropDown) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBRadioButtonList) {
				return new FIBEditableRadioButtonListWidget((FIBRadioButtonList) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBCheckboxList) {
				return new FIBEditableCheckboxListWidget((FIBCheckboxList) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBList) {
				return new FIBEditableListWidget((FIBList) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBNumber) {
				FIBNumber w = (FIBNumber) fibWidget;
				switch (w.getNumberType()) {
				case ByteType:
					return new FIBEditableNumberWidget.FIBEditableByteWidget(w, FIBEditorController.this);
				case ShortType:
					return new FIBEditableNumberWidget.FIBEditableShortWidget(w, FIBEditorController.this);
				case IntegerType:
					return new FIBEditableNumberWidget.FIBEditableIntegerWidget(w, FIBEditorController.this);
				case LongType:
					return new FIBEditableNumberWidget.FIBEditableLongWidget(w, FIBEditorController.this);
				case FloatType:
					return new FIBEditableNumberWidget.FIBEditableFloatWidget(w, FIBEditorController.this);
				case DoubleType:
					return new FIBEditableNumberWidget.FIBEditableDoubleWidget(w, FIBEditorController.this);
				default:
					break;
				}
			}
			if (fibWidget instanceof FIBColor) {
				return new FIBEditableColorWidget((FIBColor) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBFont) {
				return new FIBEditableFontWidget((FIBFont) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBFile) {
				return new FIBEditableFileWidget((FIBFile) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBButton) {
				return new FIBEditableButtonWidget((FIBButton) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBCustom) {
				return new FIBEditableCustomWidget((FIBCustom) fibWidget, FIBEditorController.this);
			}
			return null;
		}
	}

}
