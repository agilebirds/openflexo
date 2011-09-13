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
package org.openflexo.view.controller;

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.selection.SelectionManager;


public class FlexoFIBController<T> extends FIBController<T> implements GraphicalFlexoObserver {

    private static final Logger logger = Logger.getLogger(FlexoFIBController.class.getPackage().getName());

    private FlexoController controller;

	public FlexoFIBController(FIBComponent component)
	{
		super(component);
	}

	public FlexoFIBController(FIBComponent component, FlexoController controller)
	{
		super(component);
		this.controller = controller;
	}

	public FlexoController getFlexoController()
	{
		return controller;
	}

	public void setFlexoController(FlexoController aController)
	{
		controller = aController;
	}
	
	public FlexoEditor getEditor()
	{
		if (getFlexoController() != null) return getFlexoController().getEditor();
		return null;
	}

	public SelectionManager getSelectionManager()
	{
		if (getFlexoController() instanceof SelectionManagingController)
			return ((SelectionManagingController)getFlexoController()).getSelectionManager();
		return null;
	}

	@Override
	public void update(FlexoObservable o, DataModification dataModification)
	{
		getRootView().updateDataObject(getDataObject());
	}

	@Override
	public void setDataObject(T anObject)
	{
		if (anObject != getDataObject()) {
			if (getDataObject() instanceof FlexoObservable) ((FlexoObservable)getDataObject()).deleteObserver(this);
			super.setDataObject(anObject);
			if (anObject instanceof FlexoObservable) ((FlexoObservable)anObject).addObserver(this);
		}


		logger.fine("Set DataObject with "+anObject);
		super.setDataObject(anObject);
	}

	public void singleClick(FlexoModelObject object)
	{
		if (getFlexoController() != null) {
			getFlexoController().objectWasClicked(object);
		}
	}

	public void doubleClick(FlexoModelObject object)
	{
		if (getFlexoController() != null) {
			getFlexoController().objectWasDoubleClicked(object);
		}
	}

}
