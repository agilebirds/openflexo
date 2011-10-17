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

import javax.swing.JPanel;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.view.widget.FIBRadioButtonListWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableRadioButtonListWidget extends FIBRadioButtonListWidget implements FIBEditableView<FIBRadioButtonList, JPanel> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableRadioButtonListWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBRadioButtonList, JPanel> delegate;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController()
	{
		return editorController;
	}

	public FIBEditableRadioButtonListWidget(FIBRadioButtonList model, FIBEditorController editorController)
	{
		super(model,editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBRadioButtonList, JPanel>(this);
		model.addObserver(this);
	}


	@Override
	public void delete()
	{
		delegate.delete();
		getComponent().deleteObserver(this);
		super.delete();
	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders()
	{
		return null;
	}

	@Override
	public FIBEditableViewDelegate<FIBRadioButtonList, JPanel> getDelegate()
	{
		return delegate;
	}

	@Override
	public void update(Observable o, Object dataModification)
	{
		if (dataModification instanceof FIBModelNotification) {
			delegate.receivedModelNotifications(o, (FIBModelNotification)dataModification);
		}
	}

}
