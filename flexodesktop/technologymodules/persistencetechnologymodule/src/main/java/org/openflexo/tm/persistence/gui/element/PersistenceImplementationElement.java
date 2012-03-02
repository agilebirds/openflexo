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
package org.openflexo.tm.persistence.gui.element;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.sgmodule.controller.browser.TechnologyModuleBrowserElement;
import org.openflexo.tm.persistence.gui.PersistenceIconLibrary;
import org.openflexo.tm.persistence.impl.HibernateModel;
import org.openflexo.tm.persistence.impl.PersistenceImplementation;

public class PersistenceImplementationElement extends TechnologyModuleBrowserElement<PersistenceImplementation> {

	public PersistenceImplementationElement(PersistenceImplementation implementation, ProjectBrowser browser, BrowserElement parent) {
		super(implementation, browser, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buildChildrenVector() {
		if (getObject() != null) {
			for (HibernateModel hibernateModel : getObject().getModels()) {
				addToChilds(hibernateModel);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Icon getIcon() {
		return PersistenceIconLibrary.REPOSITORY_ICON;
	}
}
