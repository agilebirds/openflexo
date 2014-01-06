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

import javax.naming.InvalidNameException;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.dm.ProcessAddedToFolder;
import org.openflexo.foundation.wkf.dm.ProcessFolderAdded;
import org.openflexo.foundation.wkf.dm.ProcessFolderRemoved;
import org.openflexo.foundation.wkf.dm.ProcessNodeInserted;
import org.openflexo.foundation.wkf.dm.ProcessNodeRemoved;
import org.openflexo.foundation.wkf.dm.ProcessRemovedFromFolder;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.WKFIconLibrary;

/**
 * Browser element representing the workflow
 * 
 * @author sguerin
 * @deprecated use ProcessElement because it has many impact to use the FlexoProcessNode instead (one of the main reason is that getObject
 *             returns a FlexoProcessNode instead of a FlexoProcess and many actions are registered on the FlexoProcess and not on
 *             FlexoProcessNode)
 */
@Deprecated
public class FlexoProcessNodeElement extends BrowserElement {

	@Deprecated
	public FlexoProcessNodeElement(FlexoProcessNode node, ProjectBrowser browser, BrowserElement parent) {
		super(node, BrowserElementType.PROCESS_NODE, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {

		for (Enumeration<ProcessFolder> en = getProcessNode().getSortedFolders(); en.hasMoreElements();) {
			addToChilds(en.nextElement());
		}
		for (Enumeration<FlexoProcessNode> en = getProcessNode().getSortedOrphanSubprocesses(); en.hasMoreElements();) {
			addToChilds(en.nextElement().getProcess());
		}
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public String getName() {
		return getProcessNode().getProcess().getName();
	}

	@Override
	public void setName(String aName) throws FlexoException {
		try {
			getProcessNode().getProcess().setName(aName);
		} catch (InvalidNameException e) {
			throw new FlexoException(e.getLocalizedMessage(), e);
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

	private FlexoProcess getFlexoProcess() {
		return getProcessNode().getProcess();
	}

	public FlexoProcessNode getProcessNode() {
		return (FlexoProcessNode) getObject();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ProcessNodeInserted || dataModification instanceof ProcessNodeRemoved
				|| dataModification instanceof ProcessFolderAdded || dataModification instanceof ProcessFolderRemoved
				|| dataModification instanceof ProcessAddedToFolder || dataModification instanceof ProcessRemovedFromFolder) {
			refreshWhenPossible();
		} else {
			super.update(observable, dataModification);
		}
	}

}
