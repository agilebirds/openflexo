package org.openflexo.diagram.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.view.diagram.DiagramTechnologyAdapter;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class DiagramTechnologyAdapterController extends TechnologyAdapterController<DiagramTechnologyAdapter> {

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		// TODO
		return VEIconLibrary.VIEW_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return VEIconLibrary.VIEW_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return VEIconLibrary.VIEW_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return VEIconLibrary.VIEW_ICON;
	}

	/**
	 * Return icon representating supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		return null;
	}

}
