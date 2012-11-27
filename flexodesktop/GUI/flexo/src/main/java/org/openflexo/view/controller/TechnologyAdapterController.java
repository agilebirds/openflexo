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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

/**
 * This class represents a technology adapter controller<br>
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyAdapterController<TA extends TechnologyAdapter<?, ?, ?>> {

	private static final Logger logger = Logger.getLogger(TechnologyAdapterController.class.getPackage().getName());

	static {
		loadTechnologyAdapterControllers();
	}

	private static Map<TechnologyAdapter, TechnologyAdapterController<?>> loadedAdapterControllers;

	/**
	 * Retrieve all {@link TechnologyAdapterController} available from classpath. <br>
	 * Map contains the TechnologyAdapterController class name as key and the TechnologyAdapterController itself as value.
	 * 
	 * @return the retrieved TechnologyModuleDefinition map.
	 */
	public static Map<TechnologyAdapter, TechnologyAdapterController<?>> loadTechnologyAdapterControllers() {
		if (loadedAdapterControllers == null) {
			loadedAdapterControllers = new Hashtable<TechnologyAdapter, TechnologyAdapterController<?>>();
			logger.info("Loading available technology adapter controllers...");
			ServiceLoader<TechnologyAdapterController> loader = ServiceLoader.load(TechnologyAdapterController.class);
			Iterator<TechnologyAdapterController> iterator = loader.iterator();
			while (iterator.hasNext()) {
				TechnologyAdapterController technologyAdapterController = iterator.next();
				TechnologyAdapter technologyAdapter = technologyAdapterController.getTechnologyAdapter();

				logger.info("Load " + technologyAdapterController.getClass().getName() + " as controller for "
						+ technologyAdapter.getName());

				if (loadedAdapterControllers.containsKey(technologyAdapter)) {
					logger.severe("Cannot include TechnologyAdapterController with classname '" + technologyAdapter.getClass().getName()
							+ "' because it already exists !!!! A TechnologyAdapterController name MUST be unique !");
				} else {
					loadedAdapterControllers.put(technologyAdapter, technologyAdapterController);
				}
			}
			logger.info("Loading available technology adapters. Done.");
		}

		return loadedAdapterControllers;
	}

	/**
	 * Return loaded technology adapter mapping supplied class<br>
	 * If adapter is not loaded, return null
	 * 
	 * @param technologyAdapterClass
	 * @return
	 */
	public static <TAC extends TechnologyAdapterController<?>> TAC getTechnologyAdapter(Class<TAC> technologyAdapterClass) {
		return (TAC) loadedAdapterControllers.get(technologyAdapterClass);
	}

	/**
	 * Iterates over loaded technology adapters
	 * 
	 * @return
	 */
	public static Collection<TechnologyAdapterController<?>> getLoadedAdapterControllers() {
		return loadedAdapterControllers.values();
	}

	/**
	 * Return TechnologyAdapter
	 * 
	 * @return
	 */
	public abstract TA getTechnologyAdapter();

	public abstract void initializeActions(ControllerActionInitializer actionInitializer);

}
