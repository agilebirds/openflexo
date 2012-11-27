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
package org.openflexo.foundation.technologyadapter;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * This class represents a technology adapter<br>
 * A {@link TechnologyAdapter} is plugin loaded at run-time which defines and implements the required A.P.I used to connect Flexo Modelling
 * Language Virtual Machine to a technology.<br>
 * 
 * Note: this code was partially adapted from Nicolas Daniels (Blue Pimento team)
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyAdapter<M extends FlexoModel<MM>, MM extends FlexoMetaModel, MS extends ModelSlot<M, MM>> {

	private static final Logger logger = Logger.getLogger(TechnologyAdapter.class.getPackage().getName());

	static {
		loadTechnologyAdapters();
	}

	private static Map<Class, TechnologyAdapter<?, ?, ?>> loadedAdapters;

	/**
	 * Retrieve all {@link TechnologyAdapter} available from classpath. <br>
	 * Map contains the TechnologyAdapter class name as key and the TechnologyAdapter itself as value.
	 * 
	 * @return the retrieved TechnologyModuleDefinition map.
	 */
	public static Map<Class, TechnologyAdapter<?, ?, ?>> loadTechnologyAdapters() {
		if (loadedAdapters == null) {
			loadedAdapters = new Hashtable<Class, TechnologyAdapter<?, ?, ?>>();
			logger.info("Loading available technology adapters...");
			ServiceLoader<TechnologyAdapter> loader = ServiceLoader.load(TechnologyAdapter.class);
			Iterator<TechnologyAdapter> iterator = loader.iterator();
			while (iterator.hasNext()) {
				TechnologyAdapter technologyAdapter = iterator.next();

				logger.info("Load " + technologyAdapter.getName() + " as " + technologyAdapter.getClass());

				if (loadedAdapters.containsKey(technologyAdapter.getClass())) {
					logger.severe("Cannot include TechnologyAdapter with classname '" + technologyAdapter.getClass().getName()
							+ "' because it already exists !!!! A TechnologyAdapter name MUST be unique !");
				} else {
					loadedAdapters.put(technologyAdapter.getClass(), technologyAdapter);
				}
			}
			logger.info("Loading available technology adapters. Done.");
		}

		return loadedAdapters;
	}

	/**
	 * Return loaded technology adapter mapping supplied class<br>
	 * If adapter is not loaded, return null
	 * 
	 * @param technologyAdapterClass
	 * @return
	 */
	public static <TA extends TechnologyAdapter<?, ?, ?>> TA getTechnologyAdapter(Class<TA> technologyAdapterClass) {
		return (TA) loadedAdapters.get(technologyAdapterClass);
	}

	/**
	 * Iterates over loaded technology adapters
	 * 
	 * @return
	 */
	public static Collection<TechnologyAdapter<?, ?, ?>> getLoadedAdapters() {
		return loadedAdapters.values();
	}

	/**
	 * Return human-understandable name for this technology adapter<br>
	 * Unique id to consider must be the class name
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Creates a new ModelSlot
	 * 
	 * @return a new {@link ModelSlot}
	 */
	protected abstract MS createNewModelSlot();

	/**
	 * Return flag indicating if supplied file represents a valid XSD schema
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public abstract boolean isValidMetaModelFile(File aMetaModelFile);

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public abstract String retrieveMetaModelURI(File aMetaModelFile);

	/**
	 * Instantiate new meta model stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public abstract MM loadMetaModel(File aMetaModelFile, OntologyLibrary library);

	/**
	 * Return flag indicating if supplied file represents a valid XML model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModel
	 * @return
	 */
	public abstract boolean isValidModelFile(File aModelFile, MM metaModel);

	/**
	 * Creates new model conform to the supplied meta model
	 * 
	 * @param project
	 * @param metaModel
	 * @return
	 */
	public abstract M createNewModel(FlexoProject project, MM metaModel);

}
