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
package org.openflexo.components.browser.ie;

import java.util.Enumeration;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.widget.IESequenceTab;

/**
 * @author bmangez
 * 
 *         <B>Class Description</B>
 */
public class IETabContainerElement extends IEElement {

	/**
	 * @param widget
	 * @param browser
	 */
	public IETabContainerElement(IESequenceTab widget, ProjectBrowser browser, BrowserElement parent) {
		super(widget, BrowserElementType.TAB_CONTAINER, browser, parent);
		widget.getButtons().addObserver(this);
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.components.browser.BrowserElement#delete()
	 */
	@Override
	public void delete() {
		getTabContainer().getButtons().deleteObserver(this);
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		// The tabs of this tabContainer
		for (Enumeration en = getTabContainer().elements(); en.hasMoreElements();) {
			FlexoModelObject o = (FlexoModelObject) en.nextElement();
			addToChilds(o);
		}
		// The button of this tab container
		FlexoModelObject child = null;
		for (Enumeration e = getTabContainer().getButtons().elements(); e.hasMoreElements();) {
			child = (FlexoModelObject) e.nextElement();
			addToChilds(child);
		}
	}

	@Override
	public String getName() {
		if (getTabContainer().getName() == null) {
			return "TabsContainer";
		}
		return getTabContainer().getName();
	}

	protected IESequenceTab getTabContainer() {
		return (IESequenceTab) getObject();
	}

}
