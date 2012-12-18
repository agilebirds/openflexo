package org.openflexo.technologyadapter.emf.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.gui.EMFIconLibrary;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class EMFAdapterController extends TechnologyAdapterController<EMFTechnologyAdapter> {

	@Override
	public Class<EMFTechnologyAdapter> getTechnologyAdapterClass() {
		return EMFTechnologyAdapter.class;
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
		return EMFIconLibrary.XML_FILE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return EMFIconLibrary.XSD_FILE_ICON;
	}

	/**
	 * Return icon representating supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		return EMFIconLibrary.iconForObject(objectClass);
	}

	/**
	 * Return icon representating supplied pattern role
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass) {
		return null;
	}

}
