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
package org.openflexo.technologyadapter.emf.controller;

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
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
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
import org.openflexo.view.controller.TechnologyAdapterController;

public class EMFAdapterController extends TechnologyAdapterController<EMFTechnologyAdapter> {

	static final Logger logger = Logger.getLogger(EMFAdapterController.class.getPackage().getName());

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.view.controller.TechnologyAdapterController#getTechnologyAdapterClass()
	 */
	@Override
	public Class<EMFTechnologyAdapter> getTechnologyAdapterClass() {
		return EMFTechnologyAdapter.class;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.view.controller.TechnologyAdapterController#initializeActions(org.openflexo.view.controller.ControllerActionInitializer)
	 */
	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

		actionInitializer.getController().getModuleInspectorController().loadDirectory(new FileResource("Inspectors/EMF"));
	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyBigIcon() {
		return EMFIconLibrary.EMF_TECHNOLOGY_BIG_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return EMFIconLibrary.EMF_TECHNOLOGY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return EMFIconLibrary.EMF_FILE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return EMFIconLibrary.ECORE_FILE_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		return EMFIconLibrary.iconForObject(objectClass);
	}

	/**
	 * Return icon representing supplied property value
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPropertyValue(Class<? extends IFlexoOntologyPropertyValue> objectClass) {
		return EMFIconLibrary.iconForPropertyValue(objectClass);
	}

	/**
	 * Return icon representing supplied pattern role
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass) {
		if (EMFObjectIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(EMFObjectIndividual.class);
		}
		return null;
	}

	/**
	 * Return icon representing supplied edition action
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddEMFObjectIndividual.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(EMFObjectIndividual.class), IconLibrary.DUPLICATE);
		} /*else if (AddEMFClassClass.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(EMFClassClass.class), IconLibrary.DUPLICATE);
			}*/
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context) {
		if (context instanceof EMFMetaModel) {
			return new EMFMetaModelBrowserModel((EMFMetaModel) context);
		} else if (context instanceof EMFModel) {
			return new EMFModelBrowserModel((EMFModel) context);
		} else {
			logger.warning("Unexpected " + context);
			return null;
		}
	}

}
