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
import java.util.logging.Logger;

import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.action.OpenPortRegistery;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;

/**
 * Browser element representing a process not used in the parent process petri graph
 * 
 * @author sguerin
 * 
 */
public class PortRegisteryElement extends BrowserElement implements ExpansionSynchronizedElement {
	static final Logger logger = Logger.getLogger(PortRegisteryElement.class.getPackage().getName());

	public PortRegisteryElement(PortRegistery portRegistery, ProjectBrowser browser, BrowserElement parent) {
		super(portRegistery, BrowserElementType.PORT_REGISTERY, browser, parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	protected void buildChildrenVector() {
		// Adding ports
		for (Enumeration e = getPortRegistery().getSortedPorts(); e.hasMoreElements();) {
			addToChilds((FlexoPort) e.nextElement());
		}
	}

	protected PortRegistery getPortRegistery() {
		return (PortRegistery) getObject();
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		if (_browser.getSelectionManager() != null) {
			// logger.info("root focused object is "+_browser.getSelectionManager().getRootFocusedObject());
			return (getPortRegistery().getProcess() == _browser.getSelectionManager().getRootFocusedObject());
		}
		return false;
	}

	@Override
	public boolean isExpanded() {
		return getPortRegistery().getIsVisible();
	}

	@Override
	public void expand() {
		if (!getPortRegistery().getIsVisible()) {
			OpenPortRegistery.actionType.makeNewAction(getPortRegistery().getProcess(), null, getProjectBrowser().getEditor()).doAction();
			// getPortRegistery().setIsVisible(true);
		}
	}

	@Override
	public void collapse() {
		if (getPortRegistery().getIsVisible()) {
			OpenPortRegistery.actionType.makeNewAction(getPortRegistery().getProcess(), null, getProjectBrowser().getEditor()).doAction();
			// getPortRegistery().setIsVisible(false);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (_browser != null) {
			if (dataModification instanceof ObjectVisibilityChanged) {
				_browser.notifyExpansionChanged(this);
			} else {
				super.update(observable, dataModification);
			}
		}
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		return true;
	}

	@Override
	public String getName() {
		return getPortRegistery().getName();
	}

}
