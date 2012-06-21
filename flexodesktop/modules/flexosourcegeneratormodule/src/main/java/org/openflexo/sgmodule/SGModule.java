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
package org.openflexo.sgmodule;

import java.util.Collection;
import java.util.Hashtable;

import org.openflexo.ApplicationContext;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.implmodel.SGJarInspectorGroup;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.external.ExternalSGModule;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.view.controller.FlexoController;

/**
 * Source Generator module
 * 
 * @author sylvain
 */
public class SGModule extends FlexoModule implements ExternalSGModule {
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.GENERATORS, Inspectors.SG,
			SGJarInspectorGroup.INSTANCE };
	private static Hashtable<Class<? extends TechnologyModuleImplementation>, TechnologyModuleGUIFactory> recordedGUIFactories = new Hashtable<Class<? extends TechnologyModuleImplementation>, TechnologyModuleGUIFactory>();

	public SGModule(ApplicationContext applicationContext) throws Exception {
		super(applicationContext);

		// Load all available technology modules.
		TechnologyModuleDefinition.getAllTechnologyModuleDefinitions();
		SGPreferences.init();
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("build_editor"));
	}

	@Override
	public Module getModule() {
		return Module.SG_MODULE;
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new SGController(this);
	}

	public InspectorGroup getInspectorGroup() {
		return Inspectors.SG;
	}

	public SGController getSGController() {
		return (SGController) getFlexoController();
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		// Implement this
		return null;
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	/**
	 * Records a GUI factory for a technology module implementation.
	 * 
	 * @param <T>
	 *            : The technology module implementation class.
	 * @param implementationClass
	 * @param technologyModuleGUIFactory
	 */
	public static <T extends TechnologyModuleImplementation> void recordTechnologyModuleGUIFactory(Class<T> implementationClass,
			TechnologyModuleGUIFactory technologyModuleGUIFactory) {
		recordedGUIFactories.put(implementationClass, technologyModuleGUIFactory);
	}

	/**
	 * Retrieve the recorded GUI Factory for the specified TechnologyModuleImplementation class. If not found null is returned.
	 * 
	 * @param implementationClass
	 * @return the retrieved GUI Factory. Null if not found
	 */
	public static <T extends TechnologyModuleImplementation> TechnologyModuleGUIFactory getTechnologyModuleGUIFactory(
			Class<T> implementationClass) {
		return recordedGUIFactories.get(implementationClass);
	}

	/**
	 * Return all recorded GUI Factories.
	 * 
	 * @return all recorded GUI Factories.
	 */
	public static Collection<TechnologyModuleGUIFactory> getAllTechnologyModuleGUIFactories() {
		return recordedGUIFactories.values();
	}
}
