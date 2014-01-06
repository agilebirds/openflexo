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
package org.openflexo.cgmodule;

import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.external.ExternalGeneratorModule;
import org.openflexo.view.controller.FlexoController;

/**
 * Data Model Editor module
 * 
 * @author sguerin
 */
public class GeneratorModule extends FlexoModule implements ExternalGeneratorModule {

	private static final Logger logger = Logger.getLogger(GeneratorModule.class.getPackage().getName());

	public static final String GENERATOR_MODULE_SHORT_NAME = "CG";

	public static final String GENERATOR_MODULE_NAME = "code_generator";

	protected static class CodeGenerator extends Module {

		public CodeGenerator() {
			super(GENERATOR_MODULE_NAME, GENERATOR_MODULE_SHORT_NAME, "org.openflexo.cgmodule.GeneratorModule",
					"modules/flexocodegenerator", "10002", "cg", CGIconLibrary.CG_SMALL_ICON, CGIconLibrary.CG_MEDIUM_ICON,
					CGIconLibrary.CG_MEDIUM_ICON_WITH_HOVER, CGIconLibrary.CG_BIG_ICON, true);
		}

	}

	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.GENERATORS, Inspectors.CG };

	public GeneratorModule(ApplicationContext applicationContext) throws Exception {
		super(applicationContext);
		GeneratorPreferences.init();
	}

	@Override
	public Module getModule() {
		return Module.CG_MODULE;
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new GeneratorController(this);
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public GeneratorController getGeneratorController() {
		return (GeneratorController) getFlexoController();
	}

}
