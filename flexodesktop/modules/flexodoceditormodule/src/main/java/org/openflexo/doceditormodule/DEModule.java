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
package org.openflexo.doceditormodule;

import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.doceditor.DEPreferences;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.view.controller.FlexoController;

/**
 * Documentation generator module
 * 
 * @author gpolet
 */
public class DEModule extends FlexoModule {

	private static final Logger logger = Logger.getLogger(DEModule.class.getPackage().getName());

	public static final String DE_MODULE_SHORT_NAME = "DE";

	public static final String DE_MODULE_NAME = "doc_editor";

	public static class DocEditor extends Module {

		public DocEditor() {
			super(DE_MODULE_NAME, DE_MODULE_SHORT_NAME, "org.openflexo.doceditormodule.DEModule", "modules/flexodoceditor", "10005", "de",
					DEIconLibrary.DE_SMALL_ICON, DEIconLibrary.DE_MEDIUM_ICON, DEIconLibrary.DE_MEDIUM_ICON_WITH_HOVER,
					DEIconLibrary.DE_BIG_ICON, true);
		}

	}

	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.DE };

	public DEModule(ApplicationContext applicationContext) {
		super(applicationContext);
		DEPreferences.init();
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new DEController(this);
	}

	@Override
	public Module getModule() {
		return Module.DE_MODULE;
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public DEController getDEController() {
		return (DEController) getFlexoController();
	}

}
