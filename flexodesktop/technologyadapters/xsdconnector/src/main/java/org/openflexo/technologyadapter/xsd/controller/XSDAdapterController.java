package org.openflexo.technologyadapter.xsd.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.gui.XSDIconLibrary;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class XSDAdapterController extends TechnologyAdapterController<XSDTechnologyAdapter> {

	@Override
	public Class<XSDTechnologyAdapter> getTechnologyAdapterClass() {
		return XSDTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyBigIcon() {
		return XSDIconLibrary.XSD_TECHNOLOGY_BIG_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return XSDIconLibrary.XSD_TECHNOLOGY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return XSDIconLibrary.XML_FILE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return XSDIconLibrary.XSD_FILE_ICON;
	}

	/**
	 * Return icon representating supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		if (AbstractXSOntObject.class.isAssignableFrom(objectClass))
			return XSDIconLibrary.iconForObject((Class<? extends AbstractXSOntObject>) objectClass);
		return null;
	}

}
