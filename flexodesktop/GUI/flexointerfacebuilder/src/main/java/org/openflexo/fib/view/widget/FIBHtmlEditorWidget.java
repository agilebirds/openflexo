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
package org.openflexo.fib.view.widget;

import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBHtmlEditorOption;
import org.openflexo.fib.view.FIBWidgetView;

import com.metaphaseeditor.MetaphaseEditorConfiguration;
import com.metaphaseeditor.MetaphaseEditorPanel;

/**
 * Represents a widget able to edit an HTML fragment
 * 
 * @author sylvain
 */
public class FIBHtmlEditorWidget extends FIBWidgetView<FIBHtmlEditor, MetaphaseEditorPanel, String> {

	private static final Logger logger = Logger.getLogger(FIBHtmlEditorWidget.class.getPackage().getName());

	private MetaphaseEditorConfiguration _editorConfiguration;
	private final MetaphaseEditorPanel _editor;
	boolean validateOnReturn;

	public FIBHtmlEditorWidget(FIBHtmlEditor model, FIBController controller) {
		super(model, controller);
		_editor = new MetaphaseEditorPanel(_editorConfiguration = buildConfiguration());
		/* _editor.getDocument().addDocumentListener(new DocumentListener() {
		     public void changedUpdate(DocumentEvent e)
		     {
		          if ((!validateOnReturn) && (!widgetUpdating))
		              updateModelFromWidget();
		     }

		     public void insertUpdate(DocumentEvent e)
		     {
		         if ((!validateOnReturn) && (!widgetUpdating))
		             updateModelFromWidget();
		     }

		     public void removeUpdate(DocumentEvent e)
		     {
		         if ((!validateOnReturn) && (!widgetUpdating))
		             updateModelFromWidget();
		     }
		 });*/
		updateFont();
	}

	protected void updateHtmlEditorConfiguration() {
		_editor.updateComponents(_editorConfiguration = buildConfiguration());
	}

	private MetaphaseEditorConfiguration buildConfiguration() {
		MetaphaseEditorConfiguration returned = new MetaphaseEditorConfiguration();
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine1()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(1));
		}
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine2()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(2));
		}
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine3()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(3));
		}
		return returned;
	}

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		// _editor.selectAll();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), _editor.getDocument())) {
			if (modelUpdating) {
				return false;
			}
			if (getValue() != null && (getValue() + "\n").equals(_editor.getDocument())) {
				return false;
			}
			widgetUpdating = true;
			if (getValue() != null) {
				_editor.setDocument(getValue());
			}
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), _editor.getDocument())) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget() in TextAreaWidget");
			}
			setValue(_editor.getDocument());
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public MetaphaseEditorPanel getJComponent() {
		return _editor;
	}

	@Override
	public MetaphaseEditorPanel getDynamicJComponent() {
		return _editor;
	}

	@Override
	public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			_editor.setFont(getFont());
		}
	}
}
