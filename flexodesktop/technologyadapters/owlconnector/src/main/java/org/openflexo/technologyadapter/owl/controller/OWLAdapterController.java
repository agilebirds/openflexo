package org.openflexo.technologyadapter.owl.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.gui.OWLIconLibrary;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class OWLAdapterController extends TechnologyAdapterController<OWLTechnologyAdapter> {

	@Override
	public Class<OWLTechnologyAdapter> getTechnologyAdapterClass() {
		return OWLTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

		new CreateOntologyClassInitializer(actionInitializer);
		new CreateOntologyIndividualInitializer(actionInitializer);
		new CreateObjectPropertyInitializer(actionInitializer);
		new CreateDataPropertyInitializer(actionInitializer);
		new DeleteOntologyObjectsInitializer(actionInitializer);
		new AddAnnotationStatementInitializer(actionInitializer);
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return OWLIconLibrary.ONTOLOGY_LIBRARY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return OWLIconLibrary.ONTOLOGY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return OWLIconLibrary.ONTOLOGY_ICON;
	}

	/**
	 * Return icon representating supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		if (OWLObject.class.isAssignableFrom(objectClass))
			return OWLIconLibrary.iconForObject((Class<? extends OWLObject>) objectClass);
		return null;
	}

}
