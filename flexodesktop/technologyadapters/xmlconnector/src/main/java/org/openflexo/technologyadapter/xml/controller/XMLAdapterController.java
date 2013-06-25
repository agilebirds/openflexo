/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.xml.controller;

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
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.editionaction.AddXMLIndividual;
import org.openflexo.technologyadapter.xml.gui.XMLIconLibrary;
import org.openflexo.technologyadapter.xml.gui.XMLModelBrowserModel;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.viewpoint.XMLIndividualPatternRole;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class XMLAdapterController extends TechnologyAdapterController<XMLTechnologyAdapter> {

	static final Logger logger = Logger.getLogger(XMLAdapterController.class.getPackage().getName());

	@Override
	public Class<XMLTechnologyAdapter> getTechnologyAdapterClass() {
		return XMLTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

		actionInitializer.getController().getModuleInspectorController().loadDirectory(new FileResource("Inspectors/XML"));
	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyBigIcon() {
		return XMLIconLibrary.XSD_TECHNOLOGY_BIG_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return XMLIconLibrary.XSD_TECHNOLOGY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return XMLIconLibrary.XML_FILE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return XMLIconLibrary.XML_FILE_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class objectClass) {
		// TODO RENAME ????
		return XMLIconLibrary.iconForObject(objectClass);
	}

	/**
	 * Return icon representing supplied property value
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPropertyValue(Class<? extends IFlexoOntologyPropertyValue> objectClass) {
		return null;
	}

	/**
	 * Return icon representing supplied pattern role
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass) {
		if (XMLIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(XMLIndividualPatternRole.class);
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
		if (AddXMLIndividual.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(XMLIndividual.class), IconLibrary.DUPLICATE);
		} 
		return super.getIconForEditionAction(editionActionClass);
	}

	public XMLModelBrowserModel makeOntologyBrowserModel(XMLModel context) {
		if (context instanceof XMLModel) {
			return new XMLModelBrowserModel((XMLModel) context);
		} else {
			logger.warning("Unexpected " + context);
			return null;
		}
	}

	@Override
	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context) {
		// TODO Auto-generated method stub
		return null;
	}


}
