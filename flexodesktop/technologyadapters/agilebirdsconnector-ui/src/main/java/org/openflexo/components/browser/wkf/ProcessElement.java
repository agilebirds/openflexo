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
package org.openflexo.components.browser.wkf;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.dm.ProcessAddedToFolder;
import org.openflexo.foundation.wkf.dm.ProcessRemovedFromFolder;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.WKFIconLibrary;

/**
 * Browser element representing a process
 * 
 * @author sguerin
 * 
 */
public class ProcessElement extends BrowserElement {
	static final Logger logger = Logger.getLogger(OperationNodeElement.class.getPackage().getName());

	public ProcessElement(FlexoProcess process, ProjectBrowser browser, BrowserElement parent) {
		super(process, BrowserElementType.PROCESS, browser, parent);
	}

	private boolean isObserving = false;

	private void addObserver() {
		if (isObserving) {
			return;
		}
		if (getFlexoProcess().getProcessNode() != null) {
			getFlexoProcess().getProcessNode().addObserver(this);
		}
		if (getFlexoProcess().getActivityPetriGraph() != null) {
			getFlexoProcess().getActivityPetriGraph().addObserver(this);
			isObserving = true;
		}
	}

	@Override
	public void delete() {
		if (getFlexoProcess() != null) {
			if (getFlexoProcess().getProcessNode() != null) {
				getFlexoProcess().getProcessNode().deleteObserver(this);
			}
			if (getFlexoProcess().getActivityPetriGraph() != null) {
				getFlexoProcess().getActivityPetriGraph().deleteObserver(this);
				isObserving = false;
			}
		}
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Building children for process " + getName());
		}

		// We add the roles
		// addToChilds(getFlexoProcess().getRoleList());

		// We add the status
		if (getFlexoProcess().getStatusList() != null) {
			addToChilds(getFlexoProcess().getStatusList());
		}

		// We add the deadlines
		// addToChilds(getFlexoProcess().getDeadLineList());
		addToChilds(getFlexoProcess().getPortRegistery());

		// We add the service interfaces
		Vector<ServiceInterface> interfaces = getFlexoProcess().getServiceInterfaces();
		for (Enumeration<ServiceInterface> e = interfaces.elements(); e.hasMoreElements();) {
			addToChilds(e.nextElement());
		}
		// We add SubProcesses
		if (getFlexoProcess().getProcessNode() != null) {

			for (Enumeration<ProcessFolder> en = getFlexoProcess().getProcessNode().getSortedFolders(); en.hasMoreElements();) {
				addToChilds(en.nextElement());
			}
			for (Enumeration<FlexoProcessNode> en = getFlexoProcess().getProcessNode().getSortedOrphanSubprocesses(); en.hasMoreElements();) {
				addToChilds(en.nextElement().getProcess());
			}
		}
		// We add sub-process nodes and we add activity nodes
		if (getFlexoProcess().getActivityPetriGraph() != null) {
			addObserver();
			for (Enumeration<PetriGraphNode> en = getFlexoProcess().getActivityPetriGraph().getSortedNodes(); en.hasMoreElements();) {
				PetriGraphNode element = en.nextElement();
				if (!element.isGrouped()) {
					addToChilds(element);
				}
			}
		}

		for (WKFGroup group : getFlexoProcess().getActivityPetriGraph().getGroups()) {
			addToChilds(group);
			// We add operator nodes
			/*
			 * for (Enumeration e = getFlexoProcess().getAllOperatorNodes().elements(); e.hasMoreElements();) { OperatorNode operatorNode =
			 * (OperatorNode) e.nextElement(); addToChilds(operatorNode); } // We add event nodes for (Enumeration e =
			 * getFlexoProcess().getAllEventNodes().elements(); e.hasMoreElements();) { EventNode eventNode = (EventNode) e.nextElement();
			 * addToChilds(eventNode); }
			 */
			// logger.info("STOP buildChildrenVector()");
		}
	}

	@Override
	public Icon getIcon() {
		Icon icon = super.getIcon();
		IconMarker[] markers = getIconMarkers();
		if (markers != null) {
			return IconFactory.getImageIcon((ImageIcon) icon, markers);
		}
		return icon;
	}

	private IconMarker[] getIconMarkers() {
		int count = 0;
		if (getFlexoProcess().getIsWebService()) {
			count++;
		}
		IconMarker[] markers = null;
		if (count > 0) {
			markers = new IconMarker[count];
		}
		if (getFlexoProcess().getIsWebService()) {
			markers[0] = WKFIconLibrary.WS_MARKER;
		}
		return markers;
	}

	@Override
	public String getName() {
		return getFlexoProcess().getName();
	}

	public FlexoProcess getFlexoProcess() {
		return (FlexoProcess) getObject();
	}

	@Override
	public void setName(String aName) throws FlexoException {
		try {
			getFlexoProcess().setName(aName);
		} catch (DuplicateResourceException e) {
			// Abort
			throw new FlexoException(e.getLocalizedMessage(), e);
		} catch (InvalidNameException e) {
			throw new FlexoException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getFlexoProcess() && "isDeletedOnServer".equals(dataModification.propertyName())) {
			refreshWhenPossible();
		} else if (dataModification instanceof ProcessRemovedFromFolder || dataModification instanceof ProcessAddedToFolder) {
			refreshWhenPossible();
		}
		super.update(observable, dataModification);
	}
}
