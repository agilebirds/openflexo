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

import java.util.Iterator;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;

/**
 * @author bmangez <B>Class Description</B>
 */
public class IETabWidgetElement extends BrowserElement {

	private IETabWidget widget;
	private IESequenceWidget observerdRootSequenceTab;

	/**
	 * @param widget
	 * @param browser
	 */
	public IETabWidgetElement(IETabWidget tab_widget, ProjectBrowser browser, BrowserElement parent) {
		super(tab_widget, BrowserElementType.TAB_WIDGET, browser, parent);
		observerdRootSequenceTab = ((IETabWidget) getObject()).getTabComponent().getRootSequence();
		observerdRootSequenceTab.addObserver(this);
		this.widget = tab_widget;
	}

	@Override
	public void delete() {
		if (observerdRootSequenceTab != null) {
			observerdRootSequenceTab.deleteObserver(this);
		}
		super.delete();
		observerdRootSequenceTab = null;
	}

	@Override
	public String getName() {
		if (getTabWidget().getTitle() == null) {
			return "Tab";
		}
		return getTabWidget().getTitle();
	}

	protected IETabWidget getTabWidget() {
		return widget;
	}

	/**
	 * Overrides isNameEditable
	 * 
	 * @see org.openflexo.components.browser.BrowserElement#isNameEditable()
	 */
	@Override
	public boolean isNameEditable() {
		return true;
	}

	/**
	 * Overrides setName
	 * 
	 * @see org.openflexo.components.browser.BrowserElement#setName(java.lang.String)
	 */
	@Override
	public void setName(String aName) {
		getTabWidget().setTitle(aName);
	}

	/**
	 * Overrides buildChildrenVector
	 * 
	 * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
	 */
	@Override
	protected void buildChildrenVector() {
		Iterator<IEWidget> i = ((IETabWidget) getObject()).getTabComponent().getRootSequence().iterator();
		while (i.hasNext()) {
			IEWidget element = i.next();
			addToChilds(element);
		}
	}
}
