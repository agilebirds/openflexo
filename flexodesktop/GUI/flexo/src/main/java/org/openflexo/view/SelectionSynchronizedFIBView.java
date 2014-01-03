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
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.selection.SelectionListener;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.controller.FlexoController;

/**
 * Default implementation for a FIBView which is synchronized with a {@link SelectionManager}
 * 
 * @author sylvain
 * 
 */
public class SelectionSynchronizedFIBView extends FlexoFIBView implements SelectionListener, GraphicalFlexoObserver, FIBSelectionListener {
	static final Logger logger = Logger.getLogger(SelectionSynchronizedFIBView.class.getPackage().getName());

	public SelectionSynchronizedFIBView(Object representedObject, FlexoController controller, File fibFile) {
		this(representedObject, controller, fibFile, false, controller != null ? controller.willLoad(fibFile) : null);
	}

	public SelectionSynchronizedFIBView(Object representedObject, FlexoController controller, File fibFile, FlexoProgress progress) {
		this(representedObject, controller, fibFile, false, progress);
	}

	public SelectionSynchronizedFIBView(Object representedObject, FlexoController controller, File fibFile, boolean addScrollBar,
			FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, progress);
	}

	public SelectionSynchronizedFIBView(Object representedObject, FlexoController controller, String fibResourcePath, FlexoProgress progress) {
		this(representedObject, controller, fibResourcePath, false, progress);
	}

	public SelectionSynchronizedFIBView(Object representedObject, FlexoController controller, String fibResourcePath, boolean addScrollBar,
			FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibResourcePath), addScrollBar, progress);
	}

	protected SelectionSynchronizedFIBView(Object representedObject, FlexoController controller, FIBComponent fibComponent,
			boolean addScrollBar, FlexoProgress progress) {
		super(representedObject, controller, fibComponent, addScrollBar, progress);
		getFIBView().getController().addSelectionListener(this);
		if (controller != null && controller.getSelectionManager() != null) {
			controller.getSelectionManager().addToSelectionListeners(this);
		}
	}

	@Override
	public void deleteView() {
		getFIBView().getController().removeSelectionListener(this);
		getFlexoController().getSelectionManager().removeFromSelectionListeners(this);
		super.deleteView();
	}

	/**
	 * Adds specified object to selection
	 * 
	 * @param object
	 */
	@Override
	public void fireObjectSelected(FlexoObject object) {
		if (ignoreFiredSelectionEvents) {
			return;
		}
		// logger.info("SELECTED: "+object);
		getFIBView().getController().objectAddedToSelection(getRelevantObject(object));
	}

	/**
	 * Removes specified object from selection
	 * 
	 * @param object
	 */
	@Override
	public void fireObjectDeselected(FlexoObject object) {
		if (ignoreFiredSelectionEvents) {
			return;
		}
		// logger.info("DESELECTED: "+object);
		getFIBView().getController().objectRemovedFromSelection(getRelevantObject(object));
	}

	/**
	 * Clear selection
	 */
	@Override
	public void fireResetSelection() {
		if (ignoreFiredSelectionEvents) {
			return;
		}
		// logger.info("RESET SELECTION");
		getFIBView().getController().selectionCleared();
	}

	/**
	 * Notify that the selection manager is performing a multiple selection
	 */
	@Override
	public void fireBeginMultipleSelection() {
		if (ignoreFiredSelectionEvents) {
			return;
		}
	}

	/**
	 * Notify that the selection manager has finished to perform a multiple selection
	 */
	@Override
	public void fireEndMultipleSelection() {
		if (ignoreFiredSelectionEvents) {
			return;
		}
	}

	public SelectionManager getSelectionManager() {
		return getFlexoController().getSelectionManager();
	}

	@Override
	public void selectionChanged(List<Object> selection) {
		if (selection == null) {
			return;
		}
		Vector<FlexoObject> newSelection = new Vector<FlexoObject>();
		for (Object o : selection) {
			if (o instanceof FlexoObject) {
				newSelection.add(getRelevantObject((FlexoObject) o));
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("FlexoFIBView now impose new selection : " + newSelection);
		}
		ignoreFiredSelectionEvents = true;
		getSelectionManager().setSelectedObjects(newSelection);
		ignoreFiredSelectionEvents = false;
	}

	private boolean ignoreFiredSelectionEvents = false;

	/**
	 * We manage here an indirection with resources: resource data is used instead of resource if resource is loaded
	 * 
	 * @param object
	 * @return
	 */
	private FlexoObject getRelevantObject(FlexoObject object) {
		if (object instanceof FlexoResource<?> && ((FlexoResource<?>) object).isLoaded()) {
			try {
				return (FlexoObject) ((FlexoResource<?>) object).getResourceData(null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				e.printStackTrace();
			} catch (FlexoException e) {
				e.printStackTrace();
			}
		}
		return object;
	}
}
