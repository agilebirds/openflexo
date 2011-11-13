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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.sgmodule.controller.CodeGenerationPerspective;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.controller.action.SGControllerActionInitializer;
import org.openflexo.sgmodule.controller.browser.TechnologyModuleBrowserElement;
import org.openflexo.view.ModuleView;

/**
 * Interface used to record Technology module GUI elements provider.
 * 
 * @author Nicolas Daniels
 */
public interface TechnologyModuleGUIFactory {

	/**
	 * Create a new Browser element for the specified TechnologyModelObject. <br>
	 * Can return null if no specific element exists.
	 * 
	 * @param object
	 * @param browser
	 * @param parent
	 * @return the created Browser Element, or null.
	 */
	public <T extends TechnologyModelObject> TechnologyModuleBrowserElement<T> createBrowserElement(T object, ProjectBrowser browser,
			BrowserElement parent);

	/**
	 * Create a new model view for the specified TechnologyModelObject. <br>
	 * Can return null if no specific view exists.
	 * 
	 * @param object
	 * @param controller
	 * @param codeGenerationPerspective
	 * @return the created view or null.
	 */
	public <T extends TechnologyModelObject> ModuleView<T> createModelView(T object, SGController controller,
			CodeGenerationPerspective codeGenerationPerspective);

	/**
	 * Called at SG Module actions initialization. Used to record all Technology Module specific actions.
	 * 
	 * @param actionInitializer
	 */
	public void initializeActions(SGControllerActionInitializer actionInitializer);
}
