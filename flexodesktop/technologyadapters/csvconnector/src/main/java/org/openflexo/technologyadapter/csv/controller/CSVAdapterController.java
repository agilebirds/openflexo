/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
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

package org.openflexo.technologyadapter.csv.controller;

import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.inspector.ModuleInspectorController;
import org.openflexo.technologyadapter.csv.CSVTechnologyAdapter;
import org.openflexo.technologyadapter.csv.gui.CSVIconLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class CSVAdapterController extends TechnologyAdapterController<CSVTechnologyAdapter>
{
	static final Logger logger = Logger.getLogger(CSVAdapterController.class.getPackage().getName());

	public Class<CSVTechnologyAdapter> getTechnologyAdapterClass()
	{
		return CSVTechnologyAdapter.class;
	}

	public void initializeActions(ControllerActionInitializer actionInitializer)
	{
		actionInitializer.getController().getModuleInspectorController()
		.loadDirectory(new FileResource("src/main/resources/Inspectors/CSV"));
	}

	public ImageIcon getTechnologyBigIcon()
	{
		return CSVIconLibrary.CSV_TECHNOLOGY_BIG_ICON;
	}

	public ImageIcon getTechnologyIcon()
	{
		return CSVIconLibrary.CSV_TECHNOLOGY_ICON;
	}

	public ImageIcon getModelIcon()
	{
		return CSVIconLibrary.CSV_FILE_ICON;
	}

	public ImageIcon getMetaModelIcon()
	{
		return CSVIconLibrary.CSV_FILE_ICON;
	}

	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass)
	{
		return CSVIconLibrary.iconForObject(objectClass);
	}

	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass)
	{
		return null;
	}

	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context)
	{
		return null;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(
			Class<? extends TechnologyObject> objectClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends FlexoObject> ModuleView<T> createModuleViewForObject(
			T object, FlexoController controller, FlexoPerspective perspective) {
		// TODO Auto-generated method stub
		return null;
	} 
}