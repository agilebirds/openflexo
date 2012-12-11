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
package org.openflexo.view.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;

/**
 * This class represents a technology-specific controller provided by a {@link TechnologyAdapter}<br>
 * A {@link TechnologyAdapterController} works above conceptual layer provided by a {@link TechnologyAdapter}, and manages all tooling
 * dedicated to technology-specific management of a {@link TechnologyAdapter}<br>
 * This controller makes the bindings between Openflexo controllers/editors layer and the {@link TechnologyAdapter}
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyAdapterController<TA extends TechnologyAdapter<?, ?>> {

	private static final Logger logger = Logger.getLogger(TechnologyAdapterController.class.getPackage().getName());

	private TechnologyAdapterControllerService technologyAdapterControllerService;

	/**
	 * Returns applicable {@link TechnologyAdapterService}
	 * 
	 * @return
	 */
	public TechnologyAdapterControllerService getTechnologyAdapterControllerService() {
		return technologyAdapterControllerService;
	}

	/**
	 * Sets applicable {@link TechnologyAdapterService}
	 * 
	 * @param technologyAdapterService
	 */
	public void setTechnologyAdapterService(TechnologyAdapterControllerService technologyAdapterControllerService) {
		this.technologyAdapterControllerService = technologyAdapterControllerService;
	}

	/**
	 * Return TechnologyAdapter
	 * 
	 * @return
	 */
	public final TA getTechnologyAdapter() {
		return technologyAdapterControllerService.getFlexoServiceManager().getService(TechnologyAdapterService.class)
				.getTechnologyAdapter(getTechnologyAdapterClass());
	}

	/**
	 * Return TechnologyAdapter class
	 * 
	 * @return
	 */
	public abstract Class<TA> getTechnologyAdapterClass();

	public abstract void initializeActions(ControllerActionInitializer actionInitializer);

	public void initialize() {

	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	public abstract ImageIcon getTechnologyIcon();

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	public abstract ImageIcon getModelIcon();

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	public abstract ImageIcon getMetaModelIcon();

	/**
	 * Return icon representating supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	public abstract ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass);

}
