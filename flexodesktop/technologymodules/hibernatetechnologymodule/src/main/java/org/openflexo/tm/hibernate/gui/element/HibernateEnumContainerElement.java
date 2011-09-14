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
package org.openflexo.tm.hibernate.gui.element;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.sgmodule.controller.browser.TechnologyModuleBrowserElement;
import org.openflexo.tm.hibernate.gui.HibernateIconLibrary;
import org.openflexo.tm.hibernate.impl.HibernateEnum;
import org.openflexo.tm.hibernate.impl.HibernateEnumContainer;


public class HibernateEnumContainerElement extends TechnologyModuleBrowserElement<HibernateEnumContainer> {

	public HibernateEnumContainerElement(HibernateEnumContainer hibernateEnumContainer, ProjectBrowser browser, BrowserElement parent) {
		super(hibernateEnumContainer, browser, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buildChildrenVector() {
		if (getObject() != null) {
			for (HibernateEnum hibernateEnum : getObject().getHibernateEnums()) {
				addToChilds(hibernateEnum);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Icon getIcon() {
		return HibernateIconLibrary.ENUMCONTAINER_ICON;
	}
}
