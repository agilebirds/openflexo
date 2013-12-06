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

import javax.swing.JComponent;

import org.openflexo.fib.controller.FIBViewFactory;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableReferencedComponentWidget extends FIBReferencedComponentWidget implements
		FIBEditableView<FIBReferencedComponent, JComponent> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(FIBEditableReferencedComponentWidget.class.getPackage().getName());

	private FIBEditableViewDelegate<FIBReferencedComponent, JComponent> delegate;

	private FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableReferencedComponentWidget(FIBReferencedComponent model, FIBEditorController editorController, FIBViewFactory factory) {
		super(model, editorController.getController(), factory);
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBReferencedComponent, JComponent>(this);
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
	public FIBEditableViewDelegate<FIBReferencedComponent, JComponent> getDelegate() {
		return delegate;
	}

	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		super.receivedModelNotifications(o, propertyName, oldValue, newValue);
		if (propertyName.equals(FIBReferencedComponent.Parameters.componentFile.name())) {
			updateComponent();
		}
		delegate.receivedModelNotifications(o, propertyName, oldValue, newValue);
	}

}
