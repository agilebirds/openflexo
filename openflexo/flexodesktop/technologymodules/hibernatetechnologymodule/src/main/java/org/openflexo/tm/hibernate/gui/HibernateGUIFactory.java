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
package org.openflexo.tm.hibernate.gui;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.sgmodule.TechnologyModuleGUIFactory;
import org.openflexo.sgmodule.controller.CodeGenerationPerspective;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.controller.action.SGControllerActionInitializer;
import org.openflexo.sgmodule.controller.browser.TechnologyModuleBrowserElement;
import org.openflexo.tm.hibernate.gui.action.CreateHibernateModelActionInitializer;
import org.openflexo.tm.hibernate.gui.element.HibernateEntityElement;
import org.openflexo.tm.hibernate.gui.element.HibernateEnumContainerElement;
import org.openflexo.tm.hibernate.gui.element.HibernateEnumElement;
import org.openflexo.tm.hibernate.gui.element.HibernateImplementationElement;
import org.openflexo.tm.hibernate.gui.element.HibernateModelElement;
import org.openflexo.tm.hibernate.gui.view.HibernateEnumContainerView;
import org.openflexo.tm.hibernate.gui.view.HibernateImplementationView;
import org.openflexo.tm.hibernate.gui.view.HibernateModelView;
import org.openflexo.tm.hibernate.impl.HibernateAttribute;
import org.openflexo.tm.hibernate.impl.HibernateEntity;
import org.openflexo.tm.hibernate.impl.HibernateEnum;
import org.openflexo.tm.hibernate.impl.HibernateEnumContainer;
import org.openflexo.tm.hibernate.impl.HibernateEnumValue;
import org.openflexo.tm.hibernate.impl.HibernateImplementation;
import org.openflexo.tm.hibernate.impl.HibernateModel;
import org.openflexo.tm.hibernate.impl.HibernateRelationship;
import org.openflexo.view.ModuleView;


/**
 * @author Nicolas Daniels
 *
 */
public class HibernateGUIFactory implements TechnologyModuleGUIFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends TechnologyModelObject> TechnologyModuleBrowserElement<T> createBrowserElement(T object, ProjectBrowser browser, BrowserElement parent) {
		if (object instanceof HibernateImplementation)
			return (TechnologyModuleBrowserElement<T>) new HibernateImplementationElement((HibernateImplementation) object, browser, parent);

		if (object instanceof HibernateModel)
			return (TechnologyModuleBrowserElement<T>) new HibernateModelElement((HibernateModel) object, browser, parent);

		if (object instanceof HibernateEntity)
			return (TechnologyModuleBrowserElement<T>) new HibernateEntityElement((HibernateEntity) object, browser, parent);

		if (object instanceof HibernateEnumContainer)
			return (TechnologyModuleBrowserElement<T>) new HibernateEnumContainerElement((HibernateEnumContainer) object, browser, parent);

		if (object instanceof HibernateEnum)
			return (TechnologyModuleBrowserElement<T>) new HibernateEnumElement((HibernateEnum) object, browser, parent);

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends TechnologyModelObject> ModuleView<T> createModelView(T object, SGController controller, CodeGenerationPerspective codeGenerationPerspective) {
		if (object instanceof HibernateImplementation)
			return (ModuleView<T>) new HibernateImplementationView((HibernateImplementation) object, controller, codeGenerationPerspective);

		if (object instanceof HibernateModel)
			return (ModuleView<T>) new HibernateModelView((HibernateModel) object, controller, codeGenerationPerspective);

		if (object instanceof HibernateEntity)
			return (ModuleView<T>) new HibernateModelView(((HibernateEntity) object).getHibernateModel(), controller, codeGenerationPerspective);

		if (object instanceof HibernateAttribute)
			return (ModuleView<T>) new HibernateModelView(((HibernateAttribute) object).getHibernateEntity().getHibernateModel(), controller, codeGenerationPerspective);

		if (object instanceof HibernateRelationship)
			return (ModuleView<T>) new HibernateModelView(((HibernateRelationship) object).getHibernateEntity().getHibernateModel(), controller, codeGenerationPerspective);

		if (object instanceof HibernateEnumContainer)
			return (ModuleView<T>) new HibernateEnumContainerView((HibernateEnumContainer) object, controller, codeGenerationPerspective);

		if (object instanceof HibernateEnum)
			return (ModuleView<T>) new HibernateEnumContainerView(((HibernateEnum) object).getHibernateEnumContainer(), controller, codeGenerationPerspective);

		if (object instanceof HibernateEnumValue)
			return (ModuleView<T>) new HibernateEnumContainerView(((HibernateEnumValue) object).getHibernateEnum().getHibernateEnumContainer(), controller, codeGenerationPerspective);

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeActions(SGControllerActionInitializer actionInitializer) {
		new CreateHibernateModelActionInitializer(actionInitializer).init();
	}

}
