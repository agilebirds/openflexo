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

import javax.swing.JEditorPane;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBEditorPane;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.widget.FIBEditorPaneWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableEditorPaneWidget extends FIBEditorPaneWidget implements FIBEditableView<FIBEditorPane, JEditorPane> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableEditorPaneWidget.class.getPackage().getName());

	private FIBEditableViewDelegate<FIBEditorPane, JEditorPane> delegate;

	private FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableEditorPaneWidget(FIBEditorPane model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBEditorPane, JEditorPane>(this);
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
	public FIBEditableViewDelegate<FIBEditorPane, JEditorPane> getDelegate() {
		return delegate;
	}

	@Override
	public void update(Observable o, Object dataModification) {
		if (dataModification instanceof FIBAttributeNotification) {
			FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
			if (n.getAttribute() == FIBEditorPane.Parameters.contentType) {
				updateContentType();
			} else if (n.getAttribute() == FIBLabel.Parameters.label) {
				relayoutParentBecauseLabelChanged();
			}
		}

		if (dataModification instanceof FIBModelNotification) {
			delegate.receivedModelNotifications(o, (FIBModelNotification) dataModification);
		}
	}

	protected void relayoutParentBecauseLabelChanged() {
		FIBView parentView = getParentView();
		FIBEditorController controller = getEditorController();
		if (parentView instanceof FIBContainerView) {
			((FIBContainerView) parentView).updateLayout();
		}
		controller.notifyFocusedAndSelectedObject();
	}

}
