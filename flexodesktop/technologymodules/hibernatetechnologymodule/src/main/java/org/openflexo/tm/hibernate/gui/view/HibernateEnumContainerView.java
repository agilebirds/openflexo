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
package org.openflexo.tm.hibernate.gui.view;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.tm.hibernate.gui.controller.HibernateEnumContainerController;
import org.openflexo.tm.hibernate.impl.HibernateEnumContainer;
import org.openflexo.view.FIBModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * Represents the main panel content when a HibernateModel is selected
 * 
 * @author Nicolas Daniels
 */
public class HibernateEnumContainerView extends FIBModuleView<HibernateEnumContainer> {
	public static String HIBERNATE_ENUM_CONTAINER_VIEW_FIB_RESOURCE_PATH = "/Hibernate/Fib/EnumContainerView.fib";

	private FlexoPerspective declaredPerspective;

	public HibernateEnumContainerView(HibernateEnumContainer hibernateEnumContainer, SGController controller, FlexoPerspective perspective) {
		super(hibernateEnumContainer, controller, HIBERNATE_ENUM_CONTAINER_VIEW_FIB_RESOURCE_PATH);
		declaredPerspective = perspective;
	}

	@Override
	public SGController getFlexoController() {
		return (SGController) super.getFlexoController();
	}

	@Override
	public FlexoPerspective getPerspective() {
		return declaredPerspective;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FlexoFIBController createFibController(FIBComponent fibComponent, FlexoController controller) {
		return new HibernateEnumContainerController(fibComponent, controller);
	}
}
