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

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBAddingNotification;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.model.FIBRemovingNotification;
import org.openflexo.fib.view.widget.FIBHtmlEditorWidget;
import org.openflexo.logging.FlexoLogger;

import com.metaphaseeditor.MetaphaseEditorPanel;

public class FIBEditableHtmlEditorWidget extends FIBHtmlEditorWidget implements FIBEditableView<FIBHtmlEditor, MetaphaseEditorPanel> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableHtmlEditorWidget.class.getPackage().getName());

	private FIBEditableViewDelegate<FIBHtmlEditor, MetaphaseEditorPanel> delegate;

	private FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableHtmlEditorWidget(FIBHtmlEditor model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBHtmlEditor, MetaphaseEditorPanel>(this);
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
	public FIBEditableViewDelegate<FIBHtmlEditor, MetaphaseEditorPanel> getDelegate() {
		return delegate;
	}

	@Override
	public void update(Observable o, Object dataModification) {
		if (dataModification instanceof FIBAddingNotification || dataModification instanceof FIBRemovingNotification) {
			updateHtmlEditorConfiguration();
		} else if (dataModification instanceof FIBAttributeNotification) {
			FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
			if (n.getAttribute() == FIBHtmlEditor.Parameters.optionsInLine1 || n.getAttribute() == FIBHtmlEditor.Parameters.optionsInLine2
					|| n.getAttribute() == FIBHtmlEditor.Parameters.optionsInLine3
					|| n.getAttribute() == FIBHtmlEditor.Parameters.firstLevelOptionsInLine1
					|| n.getAttribute() == FIBHtmlEditor.Parameters.firstLevelOptionsInLine2
					|| n.getAttribute() == FIBHtmlEditor.Parameters.firstLevelOptionsInLine3) {
				updateHtmlEditorConfiguration();
			}

		}

		if (dataModification instanceof FIBModelNotification) {
			delegate.receivedModelNotifications(o, (FIBModelNotification) dataModification);
		}
	}

}
