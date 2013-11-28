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

import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fib.controller.FIBViewFactory;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableReferencedComponentWidget extends FIBReferencedComponentWidget implements
		FIBEditableView<FIBReferencedComponent, JComponent> {

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
		model.addObserver(this);
	}

	@Override
	public void delete() {
		delegate.delete();
		getComponent().deleteObserver(this);
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

	@Override
	public void update(Observable o, Object dataModification) {
		if (dataModification instanceof FIBAttributeNotification) {
			FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
			if (n.getAttribute() == FIBReferencedComponent.Parameters.componentFile) {
				updateComponent();
			}
		}

		if (dataModification instanceof FIBModelNotification) {
			delegate.receivedModelNotifications(o, (FIBModelNotification) dataModification);
		}
	}

}
