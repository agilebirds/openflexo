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

package org.openflexo.technologyadapter.mdtuml.controller;

import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.inspector.ModuleInspectorController;
import org.openflexo.technologyadapter.mdtuml.MDTUMLTechnologyAdapter;
import org.openflexo.technologyadapter.emf.gui.EMFIconLibrary;
import org.openflexo.technologyadapter.emf.gui.EMFMetaModelBrowserModel;
import org.openflexo.technologyadapter.emf.gui.EMFModelBrowserModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.technologyadapter.emf.viewpoint.EMFObjectIndividualPatternRole;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividual;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;

public class MDTUMLAdapterController extends TechnologyAdapterController<MDTUMLTechnologyAdapter>
{
	static final Logger logger = Logger.getLogger(MDTUMLAdapterController.class.getPackage().getName());

	public Class<MDTUMLTechnologyAdapter> getTechnologyAdapterClass()
	{
		return MDTUMLTechnologyAdapter.class;
	}

	public void initializeActions(ControllerActionInitializer actionInitializer)
	{
		// TODO Check this, why a src directory?
		actionInitializer.getController().getModuleInspectorController()
		.loadDirectory(new FileResource("src/main/resources/Inspectors/EMF"));
	}

	public ImageIcon getTechnologyBigIcon()
	{
		return EMFIconLibrary.EMF_TECHNOLOGY_BIG_ICON;
	}

	public ImageIcon getTechnologyIcon()
	{
		return EMFIconLibrary.EMF_TECHNOLOGY_ICON;
	}

	public ImageIcon getModelIcon()
	{
		return EMFIconLibrary.EMF_FILE_ICON;
	}

	public ImageIcon getMetaModelIcon()
	{
		return EMFIconLibrary.ECORE_FILE_ICON;
	}

	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass)
	{
		return EMFIconLibrary.iconForObject(objectClass);
	}

	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass)
	{
		if (EMFObjectIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(EMFObjectIndividual.class);
		}
		return null;
	}

	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass)
	{
		if (AddEMFObjectIndividual.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(EMFObjectIndividual.class), new IconMarker[] { IconLibrary.DUPLICATE });
		}

		return super.getIconForEditionAction(editionActionClass);
	}

	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context)
	{
		if ((context instanceof EMFMetaModel))
			return new EMFMetaModelBrowserModel((EMFMetaModel)context);
		if ((context instanceof EMFModel)) {
			return new EMFModelBrowserModel((EMFModel)context);
		}
		logger.warning("Unexpected " + context);
		return null;
	}

	@Override
	public ImageIcon getIconForPropertyValue(
			Class<? extends IFlexoOntologyPropertyValue> objectClass) {
		// TODO Auto-generated method stub
		return null;
	}
}