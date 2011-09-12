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
package org.openflexo.view;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.selection.SelectionListener;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class SelectionSynchronizedFIBView<O extends FlexoModelObject> extends FlexoFIBView<O>
implements SelectionListener, GraphicalFlexoObserver, FIBSelectionListener
{
    static final Logger logger = Logger.getLogger(SelectionSynchronizedFIBView.class.getPackage().getName());

    public SelectionSynchronizedFIBView(O representedObject, FlexoController controller, File fibFile)
    {
    	this(representedObject, controller, fibFile, false, controller.willLoad(fibFile));
    }

   public SelectionSynchronizedFIBView(O representedObject, FlexoController controller, File fibFile, FlexoProgress progress)
    {
    	this(representedObject, controller, fibFile, false, progress);
    }

    public SelectionSynchronizedFIBView(O representedObject, FlexoController controller, File fibFile, boolean addScrollBar, FlexoProgress progress)
    {
    	this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, progress);
    }

    public SelectionSynchronizedFIBView(O representedObject, FlexoController controller, String fibResourcePath, FlexoProgress progress)
    {
    	this(representedObject, controller, fibResourcePath, false, progress);
    }

    public SelectionSynchronizedFIBView(O representedObject, FlexoController controller, String fibResourcePath, boolean addScrollBar, FlexoProgress progress)
    {
    	this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibResourcePath), addScrollBar, progress);
    }

    protected SelectionSynchronizedFIBView(O representedObject, FlexoController controller, FIBComponent fibComponent, boolean addScrollBar, FlexoProgress progress)
    {
        super(representedObject,controller,fibComponent,addScrollBar, progress);
		if (controller instanceof SelectionManagingController) {
			getFIBView().getController().addSelectionListener(this);
			((SelectionManagingController)controller).getSelectionManager().addToSelectionListeners(this);
		}
    }

    @Override
	public void deleteView()
    {
		if (getFlexoController() instanceof SelectionManagingController) {
			getFIBView().getController().removeSelectionListener(this);
			((SelectionManagingController)getFlexoController()).getSelectionManager().removeFromSelectionListeners(this);
		}

		super.deleteView();
     }

   /**
     * Adds specified object to selection
     *
     * @param object
     */
	public void fireObjectSelected(FlexoModelObject object)
	{
		if (ignoreFiredSelectionEvents) return;
		//logger.info("SELECTED: "+object);
		getFIBView().getController().objectAddedToSelection(object);
	}

	/**
	 * Removes specified object from selection
	 *
	 * @param object
	 */
	public void fireObjectDeselected(FlexoModelObject object)
	{
		if (ignoreFiredSelectionEvents) return;
		//logger.info("DESELECTED: "+object);
		getFIBView().getController().objectRemovedFromSelection(object);
	}

	/**
	 * Clear selection
     */
    public void fireResetSelection()
    {
    	if (ignoreFiredSelectionEvents) return;
		//logger.info("RESET SELECTION");
		getFIBView().getController().selectionCleared();
   }

    /**
     * Notify that the selection manager is performing a multiple selection
     */
    public void fireBeginMultipleSelection()
    {
    	if (ignoreFiredSelectionEvents) return;
    }

    /**
     * Notify that the selection manager has finished to perform a multiple
     * selection
     */
    public void fireEndMultipleSelection()
    {
    	if (ignoreFiredSelectionEvents) return;
    }

    public SelectionManager getSelectionManager()
    {
    	return ((SelectionManagingController)getFlexoController()).getSelectionManager();
    }

	public void selectionChanged(Vector<Object> selection)
	{
		if (selection == null) return;
		Vector<FlexoModelObject> newSelection = new Vector<FlexoModelObject>();
		for (Object o : selection) {
			if (o instanceof FlexoModelObject) newSelection.add((FlexoModelObject)o);
		}
		logger.fine("FlexoFIBView now impose new selection : "+newSelection);
		ignoreFiredSelectionEvents = true;
		getSelectionManager().setSelectedObjects(newSelection);
		ignoreFiredSelectionEvents = false;
	}

	private boolean ignoreFiredSelectionEvents = false;
	
}
