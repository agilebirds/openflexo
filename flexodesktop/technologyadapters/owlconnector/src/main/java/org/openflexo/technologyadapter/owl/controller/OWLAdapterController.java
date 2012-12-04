package org.openflexo.technologyadapter.owl.controller;

import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class OWLAdapterController extends TechnologyAdapterController<OWLTechnologyAdapter> {

	@Override
	public OWLTechnologyAdapter getTechnologyAdapter() {
		return TechnologyAdapterService.getTechnologyAdapter(OWLTechnologyAdapter.class);
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
