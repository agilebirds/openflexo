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

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.icon.WKFIconLibrary;

/**
 * Browser element representing a Role
 * 
 * @author sguerin
 * 
 */
public class PortMapElement extends BrowserElement {

	public PortMapElement(FlexoPortMap portMap, ProjectBrowser browser, BrowserElement parent) {
		super(portMap, BrowserElementType.PORTMAP, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		// We add post conditions
		for (Enumeration e = getFlexoPortMap().getOutgoingPostConditions().elements(); e.hasMoreElements();) {
			addToChilds((FlexoPostCondition) e.nextElement());
		}
	}

	@Override
	public String getName() {
		return getFlexoPortMap().getName();
	}

	protected FlexoPortMap getFlexoPortMap() {
		return (FlexoPortMap) getObject();
	}

	@Override
	protected BrowserElementType getFilteredElementType() {
		return BrowserElementType.PORTMAP_REGISTERY;
	}

	@Override
	public Icon getIcon() {
		if ((getFlexoPortMap() != null)) {
			return WKFIconLibrary.getImageIconForPortmap(getFlexoPortMap());
		}
		return null;
	}

}
