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

import javax.swing.JLabel;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.widget.FIBImageWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableImageWidget extends FIBImageWidget implements FIBEditableView<FIBImage, JLabel> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(FIBEditableImageWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBImage, JLabel> delegate;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableImageWidget(FIBImage model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBImage, JLabel>(this);
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
	public FIBEditableViewDelegate<FIBImage, JLabel> getDelegate() {
		return delegate;
	}

	@Override
	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		super.receivedModelNotifications(o, propertyName, oldValue, newValue);
		if ((propertyName.equals(FIBImage.ALIGN_KEY))) {
			updateAlign();
		} else if ((propertyName.equals(FIBImage.IMAGE_FILE_KEY)) || (propertyName.equals(FIBImage.SIZE_ADJUSTMENT_KEY))
				|| (propertyName.equals(FIBImage.IMAGE_HEIGHT_KEY)) || (propertyName.equals(FIBImage.IMAGE_WIDTH_KEY))) {
			relayoutParentBecauseImageChanged();
		}
		delegate.receivedModelNotifications(o, propertyName, oldValue, newValue);
	}

	protected void relayoutParentBecauseImageChanged() {
		FIBContainerView<?, ?, ?> parentView = getParentView();
		FIBEditorController controller = getEditorController();
		parentView.updateLayout();
		controller.notifyFocusedAndSelectedObject();
	}

}
