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
package org.openflexo.fib.editor.view.widget;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.widget.FIBHtmlEditorWidget;
import org.openflexo.logging.FlexoLogger;

import com.metaphaseeditor.MetaphaseEditorPanel;

public class FIBEditableHtmlEditorWidget extends FIBHtmlEditorWidget implements FIBEditableView<FIBHtmlEditor, MetaphaseEditorPanel> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(FIBEditableHtmlEditorWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBHtmlEditor, MetaphaseEditorPanel> delegate;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableHtmlEditorWidget(FIBHtmlEditor model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBHtmlEditor, MetaphaseEditorPanel>(this);
		model.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void delete() {
		delegate.delete();
		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);
		super.delete();
	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders() {
		return null;
	}

	@Override
	public FIBEditableViewDelegate<FIBHtmlEditor, MetaphaseEditorPanel> getDelegate() {
		return delegate;
	}

	@Override
	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		super.receivedModelNotifications(o, propertyName, oldValue, newValue);
		if ((propertyName.equals(FIBHtmlEditor.OPTIONS_IN_LINE1_KEY)) || (propertyName.equals(FIBHtmlEditor.OPTIONS_IN_LINE2_KEY))
				|| (propertyName.equals(FIBHtmlEditor.OPTIONS_IN_LINE3_KEY))
				/*|| (propertyName.equals(FIBHtmlEditor.Parameters.firstLevelOptionsInLine1.name()))
				|| (propertyName.equals(FIBHtmlEditor.Parameters.firstLevelOptionsInLine2.name()))
				|| (propertyName.equals(FIBHtmlEditor.Parameters.firstLevelOptionsInLine3.name()))*/
				|| (propertyName.equals(FIBWidget.ICON_KEY))) {
			updateHtmlEditorConfiguration();
		}
		delegate.receivedModelNotifications(o, propertyName, oldValue, newValue);
	}

}
