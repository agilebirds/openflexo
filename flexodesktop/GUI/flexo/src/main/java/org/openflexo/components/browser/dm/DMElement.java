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
package org.openflexo.components.browser.dm;

import java.util.Enumeration;

import javax.naming.InvalidNameException;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.view.controller.FlexoController;

/**
 * Abstract browser element representing a DMObject
 * 
 * @author sguerin
 * 
 */
public abstract class DMElement extends BrowserElement {

	public DMElement(DMObject object, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent) {
		super(object, elementType, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		if (getDMObject() != null && getDMObject().getOrderedChildren() != null) {
			for (Enumeration e = getDMObject().getOrderedChildren().elements(); e.hasMoreElements();) {
				DMObject next = (DMObject) e.nextElement();
				addToChilds(next);
			}
		}
	}

	@Override
	public String getName() {
		return getDMObject().getLocalizedName();
	}

	protected DMObject getDMObject() {
		return (DMObject) getObject();
	}

	@Override
	public abstract boolean isNameEditable();

	@Override
	public void setName(String aName) {
		try {
			if (ReservedKeyword.contains(aName)) {
				throw new InvalidNameException();
			}
			getDMObject().setName(aName);
		} catch (Exception e) {
			FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name"));
		}
	}

}
