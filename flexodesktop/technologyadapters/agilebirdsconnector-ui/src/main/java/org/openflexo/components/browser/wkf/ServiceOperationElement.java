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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.icon.WKFIconLibrary;

/**
 * Browser element representing a ServiceOperation
 * 
 * @author Denis VANVYVE
 * 
 */
public class ServiceOperationElement extends BrowserElement {
	private static final Logger logger = Logger.getLogger(ServiceOperationElement.class.getPackage().getName());

	public ServiceOperationElement(ServiceOperation op, ProjectBrowser browser, BrowserElement parent) {
		super(op, BrowserElementType.SERVICE_OPERATION, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		if (getOperation().isInOperation()) {
			addToChilds(getOperation().getInputMessageDefinition());
		} else if (getOperation().isOutOperation()) {
			addToChilds(getOperation().getOutputMessageDefinition());
		} else if (getOperation().isInOutOperation()) {
			addToChilds(getOperation().getInputMessageDefinition());
			addToChilds(getOperation().getOutputMessageDefinition());
		}
		// TODO: Add Fault Message Definition

	}

	@Override
	public String getName() {
		return getOperation().getName();
	}

	protected ServiceOperation getOperation() {
		return (ServiceOperation) getObject();
	}

	@Override
	protected BrowserElementType getFilteredElementType() {
		// filtered element type should be PORT and not PORT_REGISTERY
		return BrowserElementType.SERVICE_OPERATION;
		// return BrowserElementType.PORT_REGISTERY;
	}

	@Override
	public Icon getIcon() {
		return decorateIcon(WKFIconLibrary.getSmallImageIconForServiceOperation(getOperation()));
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public void setName(String aName) {
		try {
			getOperation().setName(aName);
		} catch (DuplicateWKFObjectException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not set name of ServiceOperation to:" + aName);
				e.printStackTrace();
			}
			// to do: display warning message.
		}
	}

}
