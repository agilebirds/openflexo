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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceOperation;

/**
 * Browser element representing a process not used in the parent process petri graph
 * 
 * @author dvanvyve
 * 
 */
public class ServiceInterfaceElement extends BrowserElement {

	public ServiceInterfaceElement(ServiceInterface serviceInterface, ProjectBrowser browser, BrowserElement parent) {
		super(serviceInterface, BrowserElementType.SERVICE_INTERFACE, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		// Adding operations
		Vector operations = getServiceInterface().getOperations();
		for (Enumeration e = operations.elements(); e.hasMoreElements();) {
			addToChilds((ServiceOperation) e.nextElement());
		}
	}

	protected ServiceInterface getServiceInterface() {
		return (ServiceInterface) getObject();
	}

	@Override
	public String getName() {
		return getServiceInterface().getName();
	}

	/* public boolean isNameEditable()
	 {
	     return true;
	 }

	 public void setName(String aName)
	 {
	     getServiceInterface().setName(aName);
	 }
	*/

}
