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

import javax.swing.JPanel;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBMultipleValues;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.widget.FIBCheckboxListWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableCheckboxListWidget<T> extends FIBCheckboxListWidget<T> implements FIBEditableView<FIBCheckboxList, JPanel> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(FIBEditableCheckboxListWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBCheckboxList, JPanel> delegate;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableCheckboxListWidget(FIBCheckboxList model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBCheckboxList, JPanel>(this);
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
	public FIBEditableViewDelegate<FIBCheckboxList, JPanel> getDelegate() {
		return delegate;
	}

	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		super.receivedModelNotifications(o, propertyName, oldValue, newValue);
		if ((propertyName.equals(FIBWidget.Parameters.format.name())) || (propertyName.equals(FIBWidget.Parameters.localize.name()))
				|| (propertyName.equals(FIBCheckboxList.Parameters.columns.name()))
				|| (propertyName.equals(FIBCheckboxList.Parameters.hGap.name()))
				|| (propertyName.equals(FIBCheckboxList.Parameters.vGap.name())) || (propertyName.equals(FIBWidget.Parameters.icon.name()))
				|| (propertyName.equals(FIBMultipleValues.Parameters.showIcon.name()))) {
			rebuildCheckboxes();
		}
		delegate.receivedModelNotifications(o, propertyName, oldValue, newValue);
	}

}
