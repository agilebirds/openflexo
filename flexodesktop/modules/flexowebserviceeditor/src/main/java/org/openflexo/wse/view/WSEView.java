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
package org.openflexo.wse.view;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.tabular.CompoundTabularView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.AbstractMessageDefinition;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.WSObject;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.SelectionSynchronizedModuleView;
import org.openflexo.wse.controller.WSEController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class WSEView<O extends FlexoModelObject> extends CompoundTabularView<O> implements SelectionSynchronizedModuleView<O>,
		GraphicalFlexoObserver {
	static final Logger logger = Logger.getLogger(WSEView.class.getPackage().getName());

	public WSEView(O object, WSEController controller, String title) {
		super(object, controller, title);
		object.addObserver(this);
	}

	public WSEController getWSEController() {
		return (WSEController) getController();
	}

	/* public WSObject getWSObject()
	 {
	     return (WSObject)getModelObject();
	 }
	 
	 public WKFObject getWKFObject(){
	 		return (WKFObject) getModelObject();
	 }
	
	 public DMObject	getDMObject(){
	 		return (DMObject) getModelObject();
	 }*/

	public WSETabularView findTabularViewContaining(FlexoModelObject anObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("findTabularViewContaining() " + this + " obj: " + anObject);
		}
		if (anObject == null) {
			return null;
		}
		for (Enumeration en = getMasterTabularViews().elements(); en.hasMoreElements();) {
			WSETabularView next = (WSETabularView) en.nextElement();
			if (next.getModel().indexOf(anObject) > -1) {
				return next;
			}

			// HYPOTHESE: ca marche que pour des WSObject des WSObjects ! (hum hum...)
			if (anObject instanceof WSObject) {
				WSObject parentObject = (WSObject) ((WSObject) anObject).getParent();
				if (next.getModel().indexOf(parentObject) > -1) {
					if (next.getSelectedObjects().contains(parentObject)) {
						next.selectObject(parentObject);
					}
					for (Enumeration en2 = next.getSlaveTabularViews().elements(); en2.hasMoreElements();) {
						WSETabularView next2 = (WSETabularView) en2.nextElement();
						if (next2.getModel().indexOf(anObject) > -1) {
							return next2;
						}
					}
				}
			}

		}
		return null;
	}

	public void tryToSelect(FlexoModelObject anObject) {
		WSETabularView tabView = findTabularViewContaining(anObject);
		if (tabView != null) {
			tabView.selectObject(anObject);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// System.out.println(" UPDATE VIEW: "+this.getClass().getName() );
		if (dataModification instanceof ObjectDeleted) {
			// System.out.println("object deleted notification for object:"+ dataModification.oldValue() );
			// System.out.println("DELETION MODIF BEGIN: loadedView:"+getWSEController().getLoadedViews());
			if (dataModification.oldValue() == getModelObject()) {
				// fireObjectDeselected((FlexoModelObject)dataModification.oldValue());

				// System.out.println("delete this view");
				deleteModuleView();
			}
			// System.out.println("DELETION MODIF END: loadedView:"+getWSEController().getLoadedViews());
		}

	}

	@Override
	public O getRepresentedObject() {
		return getModelObject();
	}

	public String getTitle() {
		FlexoModelObject obj = getModelObject();
		if (obj instanceof DMObject) {
			return ((DMObject) obj).getLocalizedName();
		} else if (obj instanceof WSObject) {
			return ((WSObject) obj).getLocalizedName();
		} else if (obj instanceof FlexoProcess) {
			return ((FlexoProcess) obj).getName();
		} else if (obj instanceof FlexoPort) {
			return ((FlexoPort) obj).getName();
		} else if (obj instanceof AbstractMessageDefinition) {
			return ((AbstractMessageDefinition) obj).getName();
		} else if (obj instanceof ServiceInterface) {
			return ((ServiceInterface) obj).getName();
		} else if (obj instanceof ServiceOperation) {
			return ((ServiceOperation) obj).getName();
		}
		return null;
	}

	@Override
	public void deleteModuleView() {
		logger.info("Removing view !");
		getWSEController().removeModuleView(this);
	}

	@Override
	public FlexoPerspective<FlexoModelObject> getPerspective() {
		return getWSEController().WSE_PERSPECTIVE;
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
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
