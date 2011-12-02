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
package org.openflexo.dm.view;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.tabular.CompoundTabularView;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.SelectionSynchronizedModuleView;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class DMView<O extends DMObject> extends CompoundTabularView<O> implements SelectionSynchronizedModuleView<O>,
		GraphicalFlexoObserver {
	static final Logger logger = Logger.getLogger(DMView.class.getPackage().getName());

	public DMView(O object, DMController controller, String title) {
		super(object, controller, title);
		object.addObserver(this);
	}

	public DMController getDMController() {
		return (DMController) getController();
	}

	public O getDMObject() {
		return getModelObject();
	}

	public DMTabularView findTabularViewContaining(DMObject anObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("findTabularViewContaining() " + this + " obj: " + anObject);
		}
		if (anObject == null) {
			return null;
		}
		for (Enumeration en = getMasterTabularViews().elements(); en.hasMoreElements();) {
			DMTabularView next = (DMTabularView) en.nextElement();
			if (next.getModel().indexOf(anObject) > -1) {
				return next;
			}
			DMObject parentObject = (DMObject) anObject.getParent();
			if (next.getModel().indexOf(parentObject) > -1) {
				if (next.getSelectedObjects().contains(parentObject)) {
					next.selectObject(parentObject);
				}
				for (Enumeration en2 = next.getSlaveTabularViews().elements(); en2.hasMoreElements();) {
					DMTabularView next2 = (DMTabularView) en2.nextElement();
					if (next2.getModel().indexOf(anObject) > -1) {
						return next2;
					}
				}
			}
		}
		return null;
	}

	public void tryToSelect(DMObject anObject) {
		DMTabularView tabView = findTabularViewContaining(anObject);
		if (tabView != null) {
			tabView.selectObject(anObject);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ObjectDeleted) {
			if (dataModification.oldValue() == getDMObject()) {
				deleteModuleView();
			}
		} else if (dataModification.propertyName() != null && dataModification.propertyName().equals("name")) {
			getDMController().getFlexoFrame().updateTitle();
			updateTitlePanel();
		}

	}

	@Override
	public O getRepresentedObject() {
		return getDMObject();
	}

	@Override
	public void deleteModuleView() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Removing DM view :" + getDMObject().getName());
		}
		getDMController().removeModuleView(this);
	}

	@Override
	public FlexoPerspective<DMObject> getPerspective() {
		return getDMController().getDefaultPespective();
	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		return reply;
	}

}
