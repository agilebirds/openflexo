package org.openflexo.technologyadapter.owl.controller;

import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
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
}
